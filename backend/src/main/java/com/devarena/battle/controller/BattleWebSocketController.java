package com.devarena.battle.controller;

import com.devarena.battle.service.BattleService;
import com.devarena.security.DevArenaUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class BattleWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(BattleWebSocketController.class);

    private final BattleService battleService;

    public BattleWebSocketController(BattleService battleService) {
        this.battleService = battleService;
    }

    @MessageMapping("/battle/{battleId}/ready")
    public void handlePlayerReady(
            @DestinationVariable UUID battleId,
            Principal principal
    ) {
        UUID userId = extractUserId(principal);
        if (userId != null) {
            battleService.markPlayerReady(battleId, userId);
        }
    }

    @MessageMapping("/battle/{battleId}/coding")
    public void handlePlayerCoding(
            @DestinationVariable UUID battleId,
            Principal principal
    ) {
        UUID userId = extractUserId(principal);
        if (userId != null) {
            battleService.broadcastCodingActivity(battleId, userId);
        }
    }

    @MessageMapping("/battle/{battleId}/leave")
    public void handlePlayerLeave(
            @DestinationVariable UUID battleId,
            Principal principal
    ) {
        UUID userId = extractUserId(principal);
        if (userId != null) {
            try {
                battleService.forfeitBattle(battleId, userId);
            } catch (Exception ex) {
                log.warn("Could not process forfeit on leave for battle {} by user {}: {}", battleId, userId, ex.getMessage());
            }
        }
    }

    private UUID extractUserId(Principal principal) {
        if (principal instanceof Authentication auth) {
            if (auth.getPrincipal() instanceof DevArenaUserDetails details) {
                return details.getId();
            }
        }
        return null;
    }
}
