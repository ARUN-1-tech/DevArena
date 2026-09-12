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
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Robust database configuration for DevArena.
 * Seamlessly normalizes DATABASE_URL from cloud hosting platforms (Supabase, Render, Railway, AWS)
 * into JDBC-compliant URLs, extracting embedded credentials and configuring SSL/TLS requirements.
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
        if (parsed.jdbcUrl() != null && parsed.jdbcUrl().startsWith("jdbc:postgresql:") && isRemoteHost(parsed.jdbcUrl())) {
            config.addDataSourceProperty("ssl", "true");
            config.addDataSourceProperty("sslmode", env.getProperty("PG_SSL_MODE", "require"));
        }

        log.info("Configured Primary DataSource with normalized URL: {}", maskCredentials(parsed.jdbcUrl()));
        return new HikariDataSource(config);
    }

    public static ParsedDatabaseUrl parseDatabaseUrl(String rawUrl, String defaultUser, String defaultPassword) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return new ParsedDatabaseUrl("jdbc:h2:mem:devarena;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "sa", "");
        }

        rawUrl = rawUrl.trim();

        // Convert URI scheme postgres:// or postgresql:// to compliant JDBC URL
        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            try {
                String uriCompatible = rawUrl.startsWith("postgres://")
                        ? "http://" + rawUrl.substring("postgres://".length())
                        : "http://" + rawUrl.substring("postgresql://".length());

                URI uri = new URI(uriCompatible);

                String user = defaultUser;
                String pass = defaultPassword;
                String userInfo = uri.getUserInfo();
                if (userInfo != null && !userInfo.isBlank()) {
                    String[] parts = userInfo.split(":", 2);
                    user = parts[0];
                    if (parts.length > 1) {
                        pass = parts[1];
                    }
                }

                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                if (path == null || path.isBlank() || path.equals("/")) {
                    path = "/postgres";
                }

                String query = uri.getQuery();
                StringBuilder jdbcUrl = new StringBuilder();
                jdbcUrl.append("jdbc:postgresql://").append(host).append(":").append(port).append(path);

                if (query != null && !query.isBlank()) {
                    jdbcUrl.append("?").append(query);
                    if (!query.contains("sslmode") && isRemoteHost(host)) {
                        jdbcUrl.append("&sslmode=require");
                    }
                } else if (isRemoteHost(host)) {
                    jdbcUrl.append("?sslmode=require");
                }

                return new ParsedDatabaseUrl(jdbcUrl.toString(), user, pass);
            } catch (URISyntaxException e) {
                log.warn("Falling back to direct string normalization for DATABASE_URL: {}", e.getMessage());
                String jdbcUrl = "jdbc:" + (rawUrl.startsWith("postgres://") ? "postgresql://" + rawUrl.substring("postgres://".length()) : rawUrl);
                return new ParsedDatabaseUrl(jdbcUrl, defaultUser, defaultPassword);
            }
        }

        // If it starts with jdbc:postgresql: but doesn't have sslmode specified for remote host
        if (rawUrl.startsWith("jdbc:postgresql:") && isRemoteHost(rawUrl) && !rawUrl.contains("sslmode=")) {
            String separator = rawUrl.contains("?") ? "&" : "?";
            rawUrl = rawUrl + separator + "sslmode=require";
        }

        return new ParsedDatabaseUrl(rawUrl, defaultUser, defaultPassword);
    }

    private static boolean isRemoteHost(String hostOrUrl) {
        if (hostOrUrl == null) return false;
        String lower = hostOrUrl.toLowerCase();
        return !lower.contains("localhost") && !lower.contains("127.0.0.1") && !lower.contains("postgres:5432");
    }

    private static String maskCredentials(String url) {
        if (url == null) return "null";
        return url.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }

    public record ParsedDatabaseUrl(String jdbcUrl, String username, String password) {}
}