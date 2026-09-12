package com.devarena.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Robust database configuration for DevArena.
 * Seamlessly normalizes DATABASE_URL from cloud hosting platforms (Supabase, Render, Railway, AWS RDS)
 * into strictly standard JDBC-compliant URLs (jdbc:postgresql://HOST:PORT/DATABASE), extracting embedded
 * credentials and setting SSL/TLS requirements without exposing secrets.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        // Priority: DATABASE_URL -> SPRING_DATASOURCE_URL -> spring.datasource.url
        String rawUrl = env.getProperty("DATABASE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("SPRING_DATASOURCE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("spring.datasource.url");
        }

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        if (username == null || username.isBlank()) {
            username = env.getProperty("DATABASE_USERNAME");
        }
        if (username == null || username.isBlank()) {
            username = env.getProperty("spring.datasource.username");
        }

        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");
        if (password == null || password.isBlank()) {
            password = env.getProperty("DATABASE_PASSWORD");
        }
        if (password == null || password.isBlank()) {
            password = env.getProperty("spring.datasource.password");
        }

        String driverClassName = env.getProperty("spring.datasource.driver-class-name");

        ParsedDatabaseUrl parsed = parseDatabaseUrl(rawUrl, username, password);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(parsed.jdbcUrl());

        if (parsed.username() != null && !parsed.username().isBlank()) {
            config.setUsername(parsed.username());
        }
        if (parsed.password() != null && !parsed.password().isBlank()) {
            config.setPassword(parsed.password());
        }

        if (driverClassName != null && !driverClassName.isBlank()) {
            config.setDriverClassName(driverClassName);
        } else if (parsed.jdbcUrl() != null && parsed.jdbcUrl().startsWith("jdbc:h2:")) {
            config.setDriverClassName("org.h2.Driver");
        } else {
            config.setDriverClassName("org.postgresql.Driver");
        }

        config.setPoolName(env.getProperty("spring.datasource.hikari.pool-name", "DevArenaHikariPool"));
        
        String maxPoolSize = env.getProperty("spring.datasource.hikari.maximum-pool-size");
        config.setMaximumPoolSize(maxPoolSize != null ? Integer.parseInt(maxPoolSize) : 10);
        
        String minIdle = env.getProperty("spring.datasource.hikari.minimum-idle");
        config.setMinimumIdle(minIdle != null ? Integer.parseInt(minIdle) : 2);

        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Configure SSL properties for remote PostgreSQL connections (Supabase, Neon, AWS RDS, Render)
        if (parsed.jdbcUrl() != null && parsed.jdbcUrl().startsWith("jdbc:postgresql:") && isRemoteHost(parsed.host())) {
            config.addDataSourceProperty("ssl", "true");
            config.addDataSourceProperty("sslmode", env.getProperty("PG_SSL_MODE", "require"));
        }

        log.info("Configured Primary DataSource successfully for host [{}], database [{}]", parsed.host(), parsed.database());
        return new HikariDataSource(config);
    }

    public static ParsedDatabaseUrl parseDatabaseUrl(String rawUrl, String defaultUser, String defaultPassword) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return new ParsedDatabaseUrl("jdbc:h2:mem:devarena;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "sa", "", "localhost", "mem:devarena");
        }

        rawUrl = rawUrl.trim();

        // If it's an H2 or other non-PostgreSQL JDBC URL, return as-is
        if (rawUrl.startsWith("jdbc:") && !rawUrl.startsWith("jdbc:postgresql:") && !rawUrl.startsWith("jdbc:postgres:")) {
            return new ParsedDatabaseUrl(rawUrl, defaultUser, defaultPassword, "localhost", "");
        }

        // Determine prefix and strip it
        String remainder = rawUrl;
        if (remainder.startsWith("jdbc:postgresql://")) {
            remainder = remainder.substring("jdbc:postgresql://".length());
        } else if (remainder.startsWith("jdbc:postgres://")) {
            remainder = remainder.substring("jdbc:postgres://".length());
        } else if (remainder.startsWith("postgresql://")) {
            remainder = remainder.substring("postgresql://".length());
        } else if (remainder.startsWith("postgres://")) {
            remainder = remainder.substring("postgres://".length());
        } else if (remainder.startsWith("jdbc:postgresql:")) {
            remainder = remainder.substring("jdbc:postgresql:".length());
        }

        // Check if remainder still has double slash (e.g. if prefix was jdbc:postgresql:)
        if (remainder.startsWith("//")) {
            remainder = remainder.substring(2);
        }

        // Extract authority (user:pass@host:port) vs path/query (/dbname?params)
        int pathIdx = remainder.indexOf('/');
        int queryIdx = remainder.indexOf('?');

        int endOfAuthority;
        if (pathIdx != -1 && queryIdx != -1) {
            endOfAuthority = Math.min(pathIdx, queryIdx);
        } else if (pathIdx != -1) {
            endOfAuthority = pathIdx;
        } else if (queryIdx != -1) {
            endOfAuthority = queryIdx;
        } else {
            endOfAuthority = remainder.length();
        }

        String authority = remainder.substring(0, endOfAuthority);
        String pathAndQuery = remainder.substring(endOfAuthority);

        String user = defaultUser;
        String pass = defaultPassword;
        String hostPort = authority;

        // Check if user:pass@ is present in authority
        // We find the LAST '@' in the authority
        int atIdx = authority.lastIndexOf('@');
        if (atIdx != -1) {
            String userInfo = authority.substring(0, atIdx);
            hostPort = authority.substring(atIdx + 1);

            int colonIdx = userInfo.indexOf(':');
            if (colonIdx != -1) {
                user = userInfo.substring(0, colonIdx);
                pass = userInfo.substring(colonIdx + 1);
            } else {
                user = userInfo;
            }
        }

        // Extract host and port from hostPort
        String host = hostPort;
        String port = "5432";
        if (hostPort.startsWith("[")) {
            // IPv6 [::1]:5432
            int closingBracket = hostPort.indexOf(']');
            if (closingBracket != -1) {
                host = hostPort.substring(0, closingBracket + 1);
                if (hostPort.length() > closingBracket + 1 && hostPort.charAt(closingBracket + 1) == ':') {
                    port = hostPort.substring(closingBracket + 2);
                }
            }
        } else {
            int portColon = hostPort.lastIndexOf(':');
            if (portColon != -1) {
                host = hostPort.substring(0, portColon);
                port = hostPort.substring(portColon + 1);
            }
        }

        if (host.isBlank()) {
            host = "localhost";
        }
        if (port.isBlank()) {
            port = "5432";
        }

        // Extract database name and query string from pathAndQuery
        String dbName = "postgres";
        String queryString = "";

        if (!pathAndQuery.isEmpty()) {
            int qIdx = pathAndQuery.indexOf('?');
            if (qIdx != -1) {
                String pathPart = pathAndQuery.substring(0, qIdx);
                queryString = pathAndQuery.substring(qIdx + 1);
                if (pathPart.startsWith("/")) {
                    pathPart = pathPart.substring(1);
                }
                if (!pathPart.isBlank()) {
                    dbName = pathPart;
                }
            } else {
                String pathPart = pathAndQuery;
                if (pathPart.startsWith("/")) {
                    pathPart = pathPart.substring(1);
                }
                if (!pathPart.isBlank()) {
                    dbName = pathPart;
                }
            }
        }

        // Build clean standard JDBC URL: jdbc:postgresql://HOST:PORT/DATABASE
        StringBuilder jdbcUrl = new StringBuilder();
        jdbcUrl.append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(dbName);

        boolean remote = isRemoteHost(host);
        if (!queryString.isBlank()) {
            if (remote && !queryString.contains("sslmode")) {
                queryString = queryString + "&sslmode=require";
            }
            if (remote && !queryString.contains("ssl=")) {
                queryString = queryString + "&ssl=true";
            }
            jdbcUrl.append("?").append(queryString);
        } else if (remote) {
            jdbcUrl.append("?sslmode=require&ssl=true");
        }

        return new ParsedDatabaseUrl(jdbcUrl.toString(), user, pass, host, dbName);
    }

    private static boolean isRemoteHost(String host) {
        if (host == null || host.isBlank()) return false;
        String lower = host.toLowerCase().trim();
        return !lower.equals("localhost") && !lower.equals("127.0.0.1") && !lower.equals("postgres");
    }

    public record ParsedDatabaseUrl(String jdbcUrl, String username, String password, String host, String database) {}
}