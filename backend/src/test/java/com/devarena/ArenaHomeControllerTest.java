package com.devarena;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ArenaHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/arena/home without token returns 401 Unauthorized")
    void testArenaHomeUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/arena/home")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/arena/home authenticated returns aggregated player game hub")
    void testArenaHomeAuthenticated() throws Exception {
        String payload = """
                {
                    "email": "arenawarrior@devarena.io",
                    "password": "Password123",
                    "username": "ArenaWarrior",
                    "displayName": "Arena Warrior"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String token = root.get("data").get("accessToken").asText();

        mockMvc.perform(get("/api/v1/arena/home")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.player.username").value("ArenaWarrior"))
                .andExpect(jsonPath("$.data.progression.level").value(1))
                .andExpect(jsonPath("$.data.stats.rating").value(1000))
                .andExpect(jsonPath("$.data.dailyQuests").isArray())
                .andExpect(jsonPath("$.data.recommendedChallenges").isArray())
                .andExpect(jsonPath("$.data.nextMilestone.title").isNotEmpty());
    }
}