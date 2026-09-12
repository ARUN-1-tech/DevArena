package com.devarena.battle.service;

import com.devarena.battle.dto.CreateCustomDuelRequest;
import com.devarena.battle.dto.CustomDuelRoomDto;
import com.devarena.battle.model.BattleEntity;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.common.exception.DevArenaException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.ProfileEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.ProfileRepository;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomDuelService {

    private static final Logger log = LoggerFactory.getLogger(CustomDuelService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final ChallengeRepository challengeRepository;
    private final BattleService battleService;
    private final SimpMessagingTemplate messagingTemplate;

    // Room code -> room state
    private final Map<String, CustomDuelRoomDto> rooms = new ConcurrentHashMap<>();

    public CustomDuelService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PlayerStatsRepository playerStatsRepository,
            ChallengeRepository challengeRepository,
            BattleService battleService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.challengeRepository = challengeRepository;
        this.battleService = battleService;
        this.messagingTemplate = messagingTemplate;
    }

    public CustomDuelRoomDto createRoom(UUID hostId, CreateCustomDuelRequest req) {
        UserEntity host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Host user not found"));

        ProfileEntity profile = profileRepository.findByUserId(hostId).orElse(null);
        PlayerStatsEntity stats = playerStatsRepository.findByUserId(hostId).orElse(null);

        ChallengeEntity challenge = null;
        if (req != null && req.challengeId() != null) {
            challenge = challengeRepository.findById(req.challengeId()).orElse(null);
        }

        if (challenge == null) {
            List<ChallengeEntity> allChallenges = challengeRepository.findAll();
            if (req != null && req.difficulty() != null && !req.difficulty().equalsIgnoreCase("ANY")) {
                try {
                    ChallengeDifficulty targetDiff = ChallengeDifficulty.valueOf(req.difficulty().toUpperCase());
                    List<ChallengeEntity> filtered = allChallenges.stream()
                            .filter(c -> c.getDifficulty() == targetDiff)
                            .toList();
                    if (!filtered.isEmpty()) {
                        challenge = filtered.get(RANDOM.nextInt(filtered.size()));
                    }
                } catch (Exception ignored) {}
            }
            if (challenge == null) {
                challenge = allChallenges.isEmpty() ? null : allChallenges.get(RANDOM.nextInt(allChallenges.size()));
            }
        }

        if (challenge == null) {
            throw new DevArenaException("No challenges available to create duel room", HttpStatus.BAD_REQUEST, "NO_CHALLENGES");
        }

        int duration = (req != null && req.durationSeconds() != null && req.durationSeconds() > 0)
                ? req.durationSeconds()
                : 900;

        String roomCode = generateUniqueRoomCode();

        CustomDuelRoomDto room = new CustomDuelRoomDto(
                roomCode,
                hostId,
                host.getUsername(),
                profile != null ? profile.getDisplayName() : host.getUsername(),
                profile != null ? profile.getAvatar() : "avatar-1",
                stats != null ? stats.getRating() : 1000,
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDifficulty().name(),
                duration,
                "WAITING",
                null,
                Instant.now()
        );

        rooms.put(roomCode, room);
        log.info("Custom duel room {} created by {} for challenge '{}'", roomCode, host.getUsername(), challenge.getTitle());
        return room;
    }

    public CustomDuelRoomDto joinRoom(String code, UUID guestId) {
        if (code == null) {
            throw new DevArenaException("Room code is required", HttpStatus.BAD_REQUEST, "INVALID_CODE");
        }
        String roomCode = code.trim().toUpperCase();
        CustomDuelRoomDto room = rooms.get(roomCode);
        if (room == null) {
            throw new DevArenaException("Duel room not found: " + roomCode, HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND");
        }

        if (!"WAITING".equalsIgnoreCase(room.status())) {
            throw new DevArenaException("Duel room is already in progress or has concluded", HttpStatus.BAD_REQUEST, "ROOM_NOT_AVAILABLE");
        }

        if (room.hostId().equals(guestId)) {
            throw new DevArenaException("You cannot join your own duel room as opponent", HttpStatus.BAD_REQUEST, "CANNOT_JOIN_SELF");
        }

        UserEntity host = userRepository.findById(room.hostId())
                .orElseThrow(() -> new ResourceNotFoundException("Host user not found"));
        UserEntity guest = userRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found"));

        ChallengeEntity challenge = challengeRepository.findById(room.challengeId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + room.challengeId()));

        // Create the direct battle
        BattleEntity battle = battleService.createDirectBattle(host, guest, challenge, room.durationSeconds());

        CustomDuelRoomDto updatedRoom = new CustomDuelRoomDto(
                room.roomCode(),
                room.hostId(),
                room.hostUsername(),
                room.hostDisplayName(),
                room.hostAvatar(),
                room.hostRating(),
                room.challengeId(),
                room.challengeTitle(),
                room.difficulty(),
                room.durationSeconds(),
                "STARTED",
                battle.getId(),
                room.createdAt()
        );

        rooms.put(roomCode, updatedRoom);

        // Notify both host and guest via WebSocket
        Map<String, Object> roomEvent = Map.of(
                "type", "ROOM_STARTED",
                "roomCode", roomCode,
                "battleId", battle.getId().toString(),
                "challengeTitle", challenge.getTitle(),
                "durationSeconds", room.durationSeconds()
        );

        try {
            messagingTemplate.convertAndSend("/topic/custom-room." + roomCode, roomEvent);
            messagingTemplate.convertAndSendToUser(host.getUsername(), "/queue/match", roomEvent);
            messagingTemplate.convertAndSendToUser(guest.getUsername(), "/queue/match", roomEvent);
        } catch (Exception e) {
            log.warn("Failed to broadcast custom room start event: {}", e.getMessage());
        }

        log.info("Guest {} joined custom room {}. Battle {} commenced.", guest.getUsername(), roomCode, battle.getId());
        return updatedRoom;
    }

    public CustomDuelRoomDto getRoom(String code) {
        if (code == null) return null;
        return rooms.get(code.trim().toUpperCase());
    }

    public void cancelRoom(String code, UUID hostId) {
        if (code == null) return;
        String roomCode = code.trim().toUpperCase();
        CustomDuelRoomDto room = rooms.get(roomCode);
        if (room != null && room.hostId().equals(hostId)) {
            CustomDuelRoomDto cancelled = new CustomDuelRoomDto(
                    room.roomCode(),
                    room.hostId(),
                    room.hostUsername(),
                    room.hostDisplayName(),
                    room.hostAvatar(),
                    room.hostRating(),
                    room.challengeId(),
                    room.challengeTitle(),
                    room.difficulty(),
                    room.durationSeconds(),
                    "CANCELLED",
                    null,
                    room.createdAt()
            );
            rooms.put(roomCode, cancelled);
            messagingTemplate.convertAndSend("/topic/custom-room." + roomCode, Map.of("type", "ROOM_CANCELLED", "roomCode", roomCode));
        }
    }

    private String generateUniqueRoomCode() {
        for (int i = 0; i < 20; i++) {
            int num = 100000 + RANDOM.nextInt(900000);
            String code = String.valueOf(num);
            if (!rooms.containsKey(code)) {
                return code;
            }
        }
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
