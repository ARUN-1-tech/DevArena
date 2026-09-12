package com.devarena;

import com.devarena.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConfigTest {

    @Test
    @DisplayName("Parse Supabase pooler URI format with dotted username and special characters in password")
    void testParseSupabasePoolerUriWithSpecialChars() {
        String supabaseUrl = "postgresql://postgres.fpdayhmzvozpckkoraff:[YOUR-PASSWORD]@aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(supabaseUrl, null, null);

        // Crucial: JDBC URL must NEVER contain credentials in the hostname!
        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?sslmode=require&ssl=true");
        assertThat(result.username()).isEqualTo("postgres.fpdayhmzvozpckkoraff");
        assertThat(result.password()).isEqualTo("[YOUR-PASSWORD]");
        assertThat(result.host()).isEqualTo("aws-0-ap-southeast-1.pooler.supabase.com");
        assertThat(result.database()).isEqualTo("postgres");
    }

    @Test
    @DisplayName("Parse Supabase direct postgresql:// URI format with credentials and SSL")
    void testParseSupabaseDirectPostgresqlUri() {
        String supabaseUrl = "postgresql://postgres:MySecret123!@db.fpdayhmzvozpckkoraff.supabase.co:5432/postgres";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(supabaseUrl, null, null);

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://db.fpdayhmzvozpckkoraff.supabase.co:5432/postgres?sslmode=require&ssl=true");
        assertThat(result.username()).isEqualTo("postgres");
        assertThat(result.password()).isEqualTo("MySecret123!");
        assertThat(result.host()).isEqualTo("db.fpdayhmzvozpckkoraff.supabase.co");
    }

    @Test
    @DisplayName("Parse standard postgres:// URI format with existing query params")
    void testParsePostgresSchemeWithQuery() {
        String renderUrl = "postgres://admin_user:my_secret_pass@dpg-c123456789-a.oregon-postgres.render.com:5432/devarena_db?sslmode=require";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(renderUrl, null, null);

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://dpg-c123456789-a.oregon-postgres.render.com:5432/devarena_db?sslmode=require&ssl=true");
        assertThat(result.username()).isEqualTo("admin_user");
        assertThat(result.password()).isEqualTo("my_secret_pass");
        assertThat(result.host()).isEqualTo("dpg-c123456789-a.oregon-postgres.render.com");
    }

    @Test
    @DisplayName("Parse JDBC URL with embedded credentials: strip credentials from JDBC URL")
    void testParseJdbcUrlWithEmbeddedCredentials() {
        String jdbcWithCreds = "jdbc:postgresql://myuser:mypass@aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(jdbcWithCreds, null, null);

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require&ssl=true");
        assertThat(result.username()).isEqualTo("myuser");
        assertThat(result.password()).isEqualTo("mypass");
    }

    @Test
    @DisplayName("Parse direct JDBC URL without embedded credentials")
    void testParseDirectJdbcUrl() {
        String directJdbc = "jdbc:postgresql://db.abcxyz.supabase.co:5432/postgres";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(directJdbc, "postgres", "secret");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://db.abcxyz.supabase.co:5432/postgres?sslmode=require&ssl=true");
        assertThat(result.username()).isEqualTo("postgres");
        assertThat(result.password()).isEqualTo("secret");
    }

    @Test
    @DisplayName("Localhost and Docker postgres hosts do not enforce SSL")
    void testLocalhostAndDocker() {
        String localUrl = "jdbc:postgresql://localhost:5432/devarena";
        DatabaseConfig.ParsedDatabaseUrl resultLocal = DatabaseConfig.parseDatabaseUrl(localUrl, "devarena_user", "secret");
        assertThat(resultLocal.jdbcUrl()).isEqualTo("jdbc:postgresql://localhost:5432/devarena");

        String dockerUrl = "jdbc:postgresql://postgres:5432/devarena";
        DatabaseConfig.ParsedDatabaseUrl resultDocker = DatabaseConfig.parseDatabaseUrl(dockerUrl, "devarena_user", "secret");
        assertThat(resultDocker.jdbcUrl()).isEqualTo("jdbc:postgresql://postgres:5432/devarena");
    }

    @Test
    @DisplayName("H2 in-memory URL is preserved for tests and local development")
    void testParseH2Url() {
        String h2Url = "jdbc:h2:mem:devarenatest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(h2Url, "sa", "");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:h2:mem:devarenatest;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        assertThat(result.username()).isEqualTo("sa");
        assertThat(result.password()).isEqualTo("");
    }
}