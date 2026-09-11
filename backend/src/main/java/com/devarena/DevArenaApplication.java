package com.devarena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * DevArena - Engineering Foundation
 * "Code. Compete. Level Up."
 *
 * Main entry point for the DevArena Spring Boot backend.
 */
@SpringBootApplication
@EnableJpaAuditing
public class DevArenaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevArenaApplication.class, args);
    }
}
