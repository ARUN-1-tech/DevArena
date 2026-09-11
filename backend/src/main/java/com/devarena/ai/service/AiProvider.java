package com.devarena.ai.service;

import com.devarena.ai.dto.AiCoachRequest;
import com.devarena.ai.dto.AiCoachResponseDto;
import com.devarena.challenge.model.ChallengeEntity;

public interface AiProvider {
    AiCoachResponseDto generateResponse(AiCoachRequest request, ChallengeEntity challenge, String username, int hintLevel);
}
