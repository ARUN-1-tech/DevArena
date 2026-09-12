package com.devarena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("CORS preflight OPTIONS for https://dev-arena-kappa.vercel.app on auth endpoint is allowed")
    void testCorsPreflightForVercelOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/auth/register")
                        .header(HttpHeaders.ORIGIN, "https://dev-arena-kappa.vercel.app")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://dev-arena-kappa.vercel.app"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("GET /status from https://dev-arena-kappa.vercel.app returns 200 with CORS headers")
    void testGetStatusWithVercelOrigin() throws Exception {
        mockMvc.perform(get("/status")
                        .header(HttpHeaders.ORIGIN, "https://dev-arena-kappa.vercel.app"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://dev-arena-kappa.vercel.app"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /health from https://dev-arena-kappa.vercel.app returns 200 with CORS headers")
    void testGetHealthWithVercelOrigin() throws Exception {
        mockMvc.perform(get("/health")
                        .header(HttpHeaders.ORIGIN, "https://dev-arena-kappa.vercel.app"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://dev-arena-kappa.vercel.app"))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    @DisplayName("Vercel preview subdomain origins match pattern and receive CORS headers")
    void testVercelPreviewOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/status")
                        .header(HttpHeaders.ORIGIN, "https://devarena-git-feature-preview.vercel.app")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://devarena-git-feature-preview.vercel.app"));
    }
}