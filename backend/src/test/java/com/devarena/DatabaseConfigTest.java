package com.devarena;

import com.devarena.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConfigTest {

    @Test
    @DisplayName("Parse Supabase postgresql:// URI format with credentials and SSL")
    void testParseSupabasePostgresqlUri() {
        String supabaseUrl = "postgresql://postgres.projectref:SecretPassword123@aws-0-us-east-1.pooler.supabase.com:6543/postgres";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(supabaseUrl, null, null);

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require");
        assertThat(result.username()).isEqualTo("postgres.projectref");
        assertThat(result.password()).isEqualTo("SecretPassword123");
    }

    @Test
    @DisplayName("Parse standard postgres:// URI format with existing query params")
    void testParsePostgresSchemeWithQuery() {
        String renderUrl = "postgres://admin_user:my_secret_pass@dpg-c123456789-a.oregon-postgres.render.com:5432/devarena_db?sslmode=require";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(renderUrl, null, null);

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://dpg-c123456789-a.oregon-postgres.render.com:5432/devarena_db?sslmode=require");
        assertThat(result.username()).isEqualTo("admin_user");
        assertThat(result.password()).isEqualTo("my_secret_pass");
    }

    @Test
    @DisplayName("Parse JDBC URL directly and ensure sslmode is present for remote hosts")
    void testParseDirectJdbcUrl() {
        String directJdbc = "jdbc:postgresql://db.abcxyz.supabase.co:5432/postgres";
        DatabaseConfig.ParsedDatabaseUrl result = DatabaseConfig.parseDatabaseUrl(directJdbc, "postgres", "secret");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://db.abcxyz.supabase.co:5432/postgres?sslmode=require");
        assertThat(result.username()).isEqualTo("postgres");
        assertThat(result.password()).isEqualTo("secret");
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