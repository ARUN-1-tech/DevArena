package com.devarena.social.dto;

import java.util.List;
import java.util.UUID;

public record PlayerSearchResultDto(
        UUID id,
        String username,
        String displayName,
        String avatar,
        int level,
        int rating,
        String rankBadge,
        boolean online,
        boolean isFriend,
        boolean hasPendingRequest,
        List<String> skillHighlights
) {}
