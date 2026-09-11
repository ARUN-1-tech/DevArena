package com.devarena.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security 6 configuration establishing the security filter chain,
 * stateless session policy, exception entry point, and extension points for JWT auth.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CorsFilter corsFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint, CorsFilter corsFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.corsFilter = corsFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public discovery, health & real-time socket handshake
                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/status",
                                "/api/v1/test-validation",
                                "/ws/**",
                                "/ws-direct/**",
                                "/actuator/**",
                                "/error"
                        ).permitAll()

                        // Role-based authorization baseline (Module 03 ready)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // All other API endpoints require authentication
                        .requestMatchers("/api/v1/**").authenticated()

                        // Fallback
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
