package com.devarena.user.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserSummaryDto user
) {
    public static AuthResponse of(String accessToken, String refreshToken, UserSummaryDto user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", user);
    }
}
