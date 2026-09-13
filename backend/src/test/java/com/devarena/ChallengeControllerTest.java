package com.devarena;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChallengeRepository challengeRepository;

    private String getAuthToken() throws Exception {
        String payload = """
                {
                    "email": "challengetester@devarena.io",
                    "password": "Password123",
                    "username": "ChallengeTester",
                    "displayName": "Challenge Tester"
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
    @DisplayName("GET /api/v1/challenges without token returns 401 Unauthorized")
    void testGetChallengesWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/challenges")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/challenges without parameters returns non-empty list of seeded challenges")
    void testGetDefaultChallengesReturnsNonEmptyList() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/challenges")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.data.content[0].title").isNotEmpty())
                .andExpect(jsonPath("$.data.content[0].difficulty").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/challenges retrieves seeded challenges and supports filtering")
    void testGetChallengesAndFiltering() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/challenges")
                        .header("Authorization", "Bearer " + token)
                        .param("difficulty", "EASY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].difficulty").value("EASY"));
    }

    @Test
    @DisplayName("GET /api/v1/challenges/{id} retrieves challenge detail with problem statement")
    void testGetChallengeDetail() throws Exception {
        String token = getAuthToken();
        ChallengeEntity challenge = challengeRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/challenges/" + challenge.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value(challenge.getTitle()))
                .andExpect(jsonPath("$.data.description").isNotEmpty())
                .andExpect(jsonPath("$.data.difficulty").value(challenge.getDifficulty().name()))
                .andExpect(jsonPath("$.data.category").value(challenge.getCategory().name()));
    }

    @Test
    @DisplayName("POST /api/v1/challenges/{id}/start updates status to ATTEMPTED")
    void testStartChallenge() throws Exception {
        String token = getAuthToken();
        ChallengeEntity challenge = challengeRepository.findAll().get(0);

        mockMvc.perform(post("/api/v1/challenges/" + challenge.getId() + "/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ATTEMPTED"));
    }

    @Test
    @DisplayName("POST /api/v1/challenges/{id}/complete solves challenge and awards XP without double-claim")
    void testCompleteChallenge() throws Exception {
        String token = getAuthToken();
        ChallengeEntity challenge = challengeRepository.findAll().get(0);

        // First completion
        mockMvc.perform(post("/api/v1/challenges/" + challenge.getId() + "/complete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.xpEarned").value(challenge.getXpReward()))
                .andExpect(jsonPath("$.data.newXp").value(challenge.getXpReward()));

        // Second completion of same challenge does not grant duplicate XP
        mockMvc.perform(post("/api/v1/challenges/" + challenge.getId() + "/complete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.xpEarned").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/challenges/stats retrieves dynamic problem archive statistics")
    void testGetChallengeStats() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/challenges/stats")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalChallenges").isNumber())
                .andExpect(jsonPath("$.data.byDifficulty").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/challenges/{id}/answer evaluates non-coding MCQ/aptitude challenge")
    void testSubmitNonCodingAnswer() throws Exception {
        String token = getAuthToken();

        // Create an MCQ challenge for testing
        ChallengeEntity mcq = new ChallengeEntity(
                "Test MCQ Question",
                "test-mcq-question",
                "Which sorting algorithm is O(n log n)?",
                com.devarena.challenge.model.ChallengeDifficulty.EASY,
                com.devarena.challenge.model.ChallengeCategory.ALGORITHMS,
                com.devarena.challenge.model.ProblemType.MCQ,
                50,
                10,
                "sorting,mcq"
        );
        mcq.setOptions("[\"Merge Sort\", \"Bubble Sort\", \"Selection Sort\"]");
        mcq.setCorrectAnswer("Merge Sort");
        mcq.setSolutionApproach("Merge sort divides and merges in O(n log n)");
        mcq = challengeRepository.save(mcq);

        // Submit correct answer
        mockMvc.perform(post("/api/v1/challenges/" + mcq.getId() + "/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\": \"Merge Sort\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(true))
                .andExpect(jsonPath("$.data.rewardResult.xpEarned").value(50));
    }
}