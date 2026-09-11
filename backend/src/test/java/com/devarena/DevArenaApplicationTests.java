package com.devarena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that the Spring Boot application context and foundational beans
 * load successfully in test mode.
 */
@SpringBootTest
@ActiveProfiles("test")
class DevArenaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Application context starts and initializes all foundational beans")
    void contextLoads() {
        assertNotNull(applicationContext, "ApplicationContext should not be null on successful startup");
    }
}
