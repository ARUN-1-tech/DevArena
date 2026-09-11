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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.devarena.security.JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("1. Registration success - creates player, profile, stats, and returns JWT tokens")
    void testRegistrationSuccess() throws Exception {
        String payload = """
                {
                    "email": "player1@devarena.io",
                    "password": "secretPassword123",
                    "username": "CodeSlayer",
                    "displayName": "Code Slayer"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.username").value("CodeSlayer"))
                .andExpect(jsonPath("$.data.user.email").value("player1@devarena.io"))
                .andExpect(jsonPath("$.data.user.displayName").value("Code Slayer"));
    }

    @Test
    @DisplayName("2. Duplicate email returns 409 Conflict with friendly error message")
    void testDuplicateEmail() throws Exception {
        String payload = """
                {
                    "email": "dup@devarena.io",
                    "password": "password123",
                    "username": "UserOne",
                    "displayName": "User One"
                }
                """;

        // First registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        // Duplicate email registration
        String dupPayload = """
                {
                    "email": "dup@devarena.io",
                    "password": "password123",
                    "username": "UserTwo",
                    "displayName": "User Two"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dupPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.message").value("That email address is already registered."));
    }

    @Test
    @DisplayName("3. Duplicate username returns 409 Conflict with friendly error message")
    void testDuplicateUsername() throws Exception {
        String payload = """
                {
                    "email": "alpha@devarena.io",
                    "password": "password123",
                    "username": "UniqueWarrior",
                    "displayName": "Warrior Alpha"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        String dupPayload = """
                {
                    "email": "beta@devarena.io",
                    "password": "password123",
                    "username": "UniqueWarrior",
                    "displayName": "Warrior Beta"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dupPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_USERNAME"))
                .andExpect(jsonPath("$.message").value("That username is already taken."));
    }

    @Test
    @DisplayName("3b. Duplicate email with different case returns 409 Conflict")
    void testDuplicateEmailCaseInsensitive() throws Exception {
        String payload = """
                {
                    "email": "casecheck@devarena.io",
                    "password": "password123",
                    "username": "CaseUser1",
                    "displayName": "Case User 1"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        String dupPayload = """
                {
                    "email": "CaseCheck@DevArena.io",
                    "password": "password123",
                    "username": "CaseUser2",
                    "displayName": "Case User 2"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dupPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.message").value("That email address is already registered."));
    }

    @Test
    @DisplayName("3c. Duplicate username with different case returns 409 Conflict")
    void testDuplicateUsernameCaseInsensitive() throws Exception {
        String payload = """
                {
                    "email": "caseuser3@devarena.io",
                    "password": "password123",
                    "username": "ShadowNinja",
                    "displayName": "Shadow Ninja"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        String dupPayload = """
                {
                    "email": "caseuser4@devarena.io",
                    "password": "password123",
                    "username": "shadowninja",
                    "displayName": "Shadow Ninja 2"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dupPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_USERNAME"))
                .andExpect(jsonPath("$.message").value("That username is already taken."));
    }

    @Test
    @DisplayName("4. Login success - validates credentials and returns tokens")
    void testLoginSuccess() throws Exception {
        String regPayload = """
                {
                    "email": "loginuser@devarena.io",
                    "password": "loginPassword123",
                    "username": "LoginMaster",
                    "displayName": "Login Master"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regPayload))
                .andExpect(status().isCreated());

        String loginPayload = """
                {
                    "email": "loginuser@devarena.io",
                    "password": "loginPassword123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value("LoginMaster"));
    }

    @Test
    @DisplayName("5. Invalid credentials returns 401 Unauthorized")
    void testInvalidCredentials() throws Exception {
        String loginPayload = """
                {
                    "email": "nonexistent@devarena.io",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("6. Protected endpoint without token returns 401 Unauthorized")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("7. Authenticated /me endpoint returns profile, stats, and progression")
    void testAuthenticatedMeEndpoint() throws Exception {
        String regPayload = """
                {
                    "email": "meperson@devarena.io",
                    "password": "password123",
                    "username": "MePerson",
                    "displayName": "Me Person"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regPayload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String accessToken = jsonNode.get("data").get("accessToken").asText();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("MePerson"))
                .andExpect(jsonPath("$.data.stats.rating").value(1000))
                .andExpect(jsonPath("$.data.progression.level").value(1))
                .andExpect(jsonPath("$.data.progression.currentXp").value(0))
                .andExpect(jsonPath("$.data.progression.xpToNextLevel").value(100));
    }

    @Test
    @DisplayName("8. Stale or unknown user JWT token returns 401 Unauthorized instead of 500")
    void testStaleUserTokenReturnsUnauthorized() throws Exception {
        String staleToken = jwtTokenProvider.generateAccessToken(
                "NonExistentUserXYZ",
                java.util.Set.of(com.devarena.security.UserRole.ROLE_USER)
        );

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + staleToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
