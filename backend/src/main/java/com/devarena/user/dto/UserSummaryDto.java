package com.devarena.user.dto;

import java.util.Set;
import java.util.UUID;

public record UserSummaryDto(
        UUID id,
        String username,
        String email,
        String displayName,
        String avatar,
        Set<String> roles
) {}
