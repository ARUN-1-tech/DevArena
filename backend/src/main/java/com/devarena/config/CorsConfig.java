package com.devarena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Cross-Origin Resource Sharing (CORS) configuration for DevArena frontend clients.
 */
@Configuration
public class CorsConfig {

    @Value("${devarena.cors.allowed-origins:http://localhost:5173,http://localhost:3000,https://dev-arena-kappa.vercel.app}")
    private String allowedOrigins;

    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        List<String> origins = parseOrigins(allowedOrigins);
        if (!origins.contains("https://dev-arena-kappa.vercel.app")) {
            origins.add("https://dev-arena-kappa.vercel.app");
        }
        if (!origins.contains("http://localhost:5173")) {
            origins.add("http://localhost:5173");
        }
        if (!origins.contains("http://localhost:3000")) {
            origins.add("http://localhost:3000");
        }

        config.setAllowedOrigins(origins);
        config.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "http://localhost:*",
                "https://localhost:*"
        ));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "token"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        config.setExposedHeaders(List.of("Authorization", "Link", "X-Total-Count", "Location"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsFilter corsFilter(CorsConfigurationSource corsConfigurationSource) {
        return new CorsFilter(corsConfigurationSource);
    }

    public static List<String> parseOrigins(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>(List.of("http://localhost:5173", "http://localhost:3000", "https://dev-arena-kappa.vercel.app"));
        }
        List<String> list = new ArrayList<>();
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                while (trimmed.endsWith("/")) {
                    trimmed = trimmed.substring(0, trimmed.length() - 1);
                }
                if (!trimmed.isEmpty() && !list.contains(trimmed)) {
                    list.add(trimmed);
                }
            }
        }
        return list;
    }
}

