package com.devarena;

import com.devarena.quest.dto.DailyQuestDto;
import com.devarena.quest.model.PlayerDailyQuestEntity;
import com.devarena.quest.model.PlayerQuestStatus;
import com.devarena.quest.repository.PlayerDailyQuestRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DailyQuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerDailyQuestRepository playerDailyQuestRepository;

    private String getAuthToken() throws Exception {
        String payload = """
                {
                    "email": "questplayer@devarena.io",
                    "password": "Password123",
                    "username": "QuestMaster",
                    "displayName": "Quest Master"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("accessToken").asText();
    }

    @Test
    @DisplayName("GET /api/v1/quests/daily retrieves generated daily quests for player")
    void testGetDailyQuests() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/quests/daily")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @DisplayName("POST /api/v1/quests/{id}/claim rejects in-progress quest and approves completed quest")
    void testClaimQuestFlow() throws Exception {
        String token = getAuthToken();

        // 1. Get daily quests
        MvcResult questsResult = mockMvc.perform(get("/api/v1/quests/daily")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(questsResult.getResponse().getContentAsString());
        String questIdStr = root.get("data").get(0).get("id").asText();
        UUID playerQuestId = UUID.fromString(questIdStr);

        // 2. Try to claim before completing -> should return 400 Bad Request
        mockMvc.perform(post("/api/v1/quests/" + playerQuestId + "/claim")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // 3. Complete the quest in database
        PlayerDailyQuestEntity pq = playerDailyQuestRepository.findById(playerQuestId).orElseThrow();
        pq.setCurrentCount(pq.getTargetCount());
        pq.setStatus(PlayerQuestStatus.COMPLETED);
        playerDailyQuestRepository.save(pq);

        // 4. Claim completed quest -> should return 200 OK with XP reward and level status
        mockMvc.perform(post("/api/v1/quests/" + playerQuestId + "/claim")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CLAIMED"))
                .andExpect(jsonPath("$.data.xpEarned").isNumber())
                .andExpect(jsonPath("$.data.xpResult.leveledUp").isBoolean());

        // 5. Duplicate claim attempt -> should return 400 Bad Request
        mockMvc.perform(post("/api/v1/quests/" + playerQuestId + "/claim")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}