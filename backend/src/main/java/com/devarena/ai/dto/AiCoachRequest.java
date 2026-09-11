package com.devarena.ai.dto;

import com.devarena.ai.model.AiCoachRequestType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AiCoachRequest {

    private UUID challengeId;

    @NotNull(message = "requestType is required")
    private AiCoachRequestType requestType;

    private String currentCode;
    private String userMessage;

    public AiCoachRequest() {}

    public AiCoachRequest(UUID challengeId, AiCoachRequestType requestType, String currentCode, String userMessage) {
        this.challengeId = challengeId;
        this.requestType = requestType;
        this.currentCode = currentCode;
        this.userMessage = userMessage;
    }

    public UUID getChallengeId() { return challengeId; }
    public void setChallengeId(UUID challengeId) { this.challengeId = challengeId; }

    public AiCoachRequestType getRequestType() { return requestType; }
    public void setRequestType(AiCoachRequestType requestType) { this.requestType = requestType; }

    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
}
