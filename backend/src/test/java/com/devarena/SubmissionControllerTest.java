package com.devarena;

import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.common.data.ChallengeDataSeeder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeDataSeeder challengeDataSeeder;

    private String userToken;
    private UUID twoSumChallengeId;

    @BeforeEach
    void setUp() throws Exception {
        challengeDataSeeder.seedStarterCodesAndTestCasesIfEmpty();

        ChallengeEntity twoSum = challengeRepository.findBySlug("two-sum").orElseThrow();
        twoSumChallengeId = twoSum.getId();

        // Register a test user
        String uid = UUID.randomUUID().toString().substring(0, 8);
        String regPayload = """
                {
                    "email": "coder_%s@devarena.io",
                    "password": "Password123",
                    "username": "CodeRunner%s",
                    "displayName": "Code Runner"
                }
                """.formatted(uid, uid);

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regPayload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(regResult.getResponse().getContentAsString());
        userToken = root.get("data").get("accessToken").asText();
    }

    @Test
    @DisplayName("1. Run code unauthenticated returns 401")
    void testRunCodeUnauthenticated() throws Exception {
        String payload = """
                {
                    "challengeId": "%s",
                    "language": "JAVA",
                    "sourceCode": "class Solution {}"
                }
                """.formatted(twoSumChallengeId);

        mockMvc.perform(post("/api/v1/code/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("2. Run code with unsupported language returns 400 Bad Request")
    void testRunCodeUnsupportedLanguage() throws Exception {
        String payload = """
                {
                    "challengeId": "%s",
                    "language": "RUST",
                    "sourceCode": "fn main() {}"
                }
                """.formatted(twoSumChallengeId);

        mockMvc.perform(post("/api/v1/code/run")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("3. Run code does not leak hidden test cases")
    void testRunCodeOnlyRunsVisibleCases() throws Exception {
        // Submit code containing mock pattern
        String payload = """
                {
                    "challengeId": "%s",
                    "language": "PYTHON",
                    "sourceCode": "import sys\\nlines = sys.stdin.read().splitlines()\\nprint('[0, 1]')"
                }
                """.formatted(twoSumChallengeId);

        MvcResult result = mockMvc.perform(post("/api/v1/code/run")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.testResults").isArray())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode testResults = root.get("data").get("testResults");

        // Verify none of the returned test cases are hidden
        for (JsonNode tc : testResults) {
            org.junit.jupiter.api.Assertions.assertFalse(tc.get("hidden").asBoolean());
            org.junit.jupiter.api.Assertions.assertNotNull(tc.get("input").asText());
            org.junit.jupiter.api.Assertions.assertNotNull(tc.get("expectedOutput").asText());
        }
    }

    @Test
    @DisplayName("4. Submit code evaluation, XP progression, and duplicate solve idempotency")
    void testSubmitCodeLifecycle() throws Exception {
        // A valid Python solution for Two Sum that prints the correct expected output
        String validPython = """
import sys
lines = sys.stdin.read().strip().splitlines()
if len(lines) >= 2:
    nums = [int(x.strip()) for x in lines[0].split(",") if x.strip()]
    target = int(lines[1].strip())
    lookup = {}
    ans = []
    for i, n in enumerate(nums):
        diff = target - n
        if diff in lookup:
            ans = [lookup[diff], i]
            break
        lookup[n] = i
    print(f"[{ans[0]}, {ans[1]}]")
""";

        String submitPayload = """
                {
                    "challengeId": "%s",
                    "language": "PYTHON",
                    "sourceCode": %s
                }
                """.formatted(twoSumChallengeId, objectMapper.writeValueAsString(validPython));

        // First Submit
        MvcResult submitResult = mockMvc.perform(post("/api/v1/submissions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PASSED"))
                .andExpect(jsonPath("$.data.firstSolve").value(true))
                .andExpect(jsonPath("$.data.xpEarned").value(50))
                .andReturn();

        JsonNode submitNode = objectMapper.readTree(submitResult.getResponse().getContentAsString());
        String submissionId = submitNode.get("data").get("submissionId").asText();

        // Check submission detail
        mockMvc.perform(get("/api/v1/submissions/" + submissionId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(submissionId))
                .andExpect(jsonPath("$.data.status").value("PASSED"));

        // Check submissions history
        mockMvc.perform(get("/api/v1/challenges/" + twoSumChallengeId + "/submissions")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));

        // Check challenge progress
        mockMvc.perform(get("/api/v1/challenges/" + twoSumChallengeId + "/progress")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SOLVED"))
                .andExpect(jsonPath("$.data.attempts").value(1))
                .andExpect(jsonPath("$.data.bestResult").value("PASSED"));

        // Second Submit: should NOT award XP again!
        mockMvc.perform(post("/api/v1/submissions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PASSED"))
                .andExpect(jsonPath("$.data.firstSolve").value(false))
                .andExpect(jsonPath("$.data.xpEarned").value(0));
    }

    @Test
    @DisplayName("5. Submission history authorization prevents other users from accessing private submissions")
    void testSubmissionAuthorization() throws Exception {
        // Create user 2
        String uid2 = UUID.randomUUID().toString().substring(0, 8);
        String user2Payload = """
                {
                    "email": "coder2_%s@devarena.io",
                    "password": "Password123",
                    "username": "CodeRunner2%s",
                    "displayName": "Code Runner 2"
                }
                """.formatted(uid2, uid2);

        MvcResult user2Reg = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user2Payload))
                .andExpect(status().isCreated())
                .andReturn();

        String user2Token = objectMapper.readTree(user2Reg.getResponse().getContentAsString()).get("data").get("accessToken").asText();

        // User 1 submits
        String submitPayload = """
                {
                    "challengeId": "%s",
                    "language": "JAVASCRIPT",
                    "sourceCode": "console.log('test');"
                }
                """.formatted(twoSumChallengeId);

        MvcResult submitResult = mockMvc.perform(post("/api/v1/submissions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitPayload))
                .andExpect(status().isCreated())
                .andReturn();

        String submissionId = objectMapper.readTree(submitResult.getResponse().getContentAsString()).get("data").get("submissionId").asText();

        // User 2 attempts to get User 1's submission -> 404 ResourceNotFound
        mockMvc.perform(get("/api/v1/submissions/" + submissionId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isNotFound());
    }
}
