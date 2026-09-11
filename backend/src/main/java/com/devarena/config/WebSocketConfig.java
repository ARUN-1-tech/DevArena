package com.devarena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

/**
 * WebSocket STOMP messaging configuration for DevArena real-time interactions
 * (matchmaking events, 1v1 battle rooms, live scoreboards, and notifications).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${devarena.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory message broker prefixes for subscriptions
        registry.enableSimpleBroker("/topic", "/queue");

        // Application prefix for messages sent from client to server @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");

        // User destination prefix for point-to-point messages (e.g. private battle invites)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        // Native WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(origins)
                .withSockJS();

        // Direct WebSocket endpoint without SockJS fallback
        registry.addEndpoint("/ws-direct")
                .setAllowedOriginPatterns(origins);
    }
}
