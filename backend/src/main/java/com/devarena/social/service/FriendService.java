package com.devarena.social.service;

import com.devarena.battle.model.BattleEntity;
import com.devarena.battle.service.BattleService;
import com.devarena.challenge.model.ChallengeDifficulty;
import com.devarena.challenge.model.ChallengeEntity;
import com.devarena.challenge.repository.ChallengeRepository;
import com.devarena.common.exception.DevArenaException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.notification.model.NotificationType;
import com.devarena.notification.service.NotificationService;
import com.devarena.social.dto.FriendBattleInviteDto;
import com.devarena.social.dto.FriendDto;
import com.devarena.social.dto.FriendRequestDto;
import com.devarena.social.model.BattleInviteStatus;
import com.devarena.social.model.FriendBattleInviteEntity;
import com.devarena.social.model.FriendRequestEntity;
import com.devarena.social.model.FriendRequestStatus;
import com.devarena.social.model.FriendshipEntity;
import com.devarena.social.repository.FriendBattleInviteRepository;
import com.devarena.social.repository.FriendRequestRepository;
import com.devarena.social.repository.FriendshipRepository;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.ProfileEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.ProfileRepository;
import com.devarena.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class FriendService {

    private static final Logger log = LoggerFactory.getLogger(FriendService.class);

    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendBattleInviteRepository battleInviteRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final NotificationService notificationService;
    private final PresenceService presenceService;
    private final SocialRateLimiter rateLimiter;
    private final BattleService battleService;
    private final ChallengeRepository challengeRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public FriendService(
            FriendshipRepository friendshipRepository,
            FriendRequestRepository friendRequestRepository,
            FriendBattleInviteRepository battleInviteRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PlayerStatsRepository playerStatsRepository,
            PlayerProgressionRepository playerProgressionRepository,
            NotificationService notificationService,
            PresenceService presenceService,
            SocialRateLimiter rateLimiter,
            BattleService battleService,
            ChallengeRepository challengeRepository,
            org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate
    ) {
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.battleInviteRepository = battleInviteRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.playerProgressionRepository = playerProgressionRepository;
        this.notificationService = notificationService;
        this.presenceService = presenceService;
        this.rateLimiter = rateLimiter;
        this.battleService = battleService;
        this.challengeRepository = challengeRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public List<FriendDto> getFriends(UUID userId) {
        List<FriendshipEntity> friendships = friendshipRepository.findAllByUserId(userId);
        List<FriendDto> friends = new ArrayList<>();

        for (FriendshipEntity f : friendships) {
            UserEntity other = f.getUser1().getId().equals(userId) ? f.getUser2() : f.getUser1();
            ProfileEntity profile = profileRepository.findByUserId(other.getId()).orElse(null);
            PlayerStatsEntity stats = playerStatsRepository.findByUserId(other.getId()).orElse(null);
            PlayerProgressionEntity progression = playerProgressionRepository.findByUserId(other.getId()).orElse(null);

            boolean online = presenceService.isOnline(other.getId());

            friends.add(new FriendDto(
                    f.getId(),
                    other.getId(),
                    other.getUsername(),
                    profile != null ? profile.getDisplayName() : other.getUsername(),
                    profile != null ? profile.getAvatar() : "avatar-1",
                    progression != null ? progression.getLevel() : 1,
                    stats != null ? stats.getRating() : 1000,
                    online,
                    f.getCreatedAt()
            ));
        }

        // Sort online friends first, then by rating
        friends.sort((a, b) -> {
            if (a.online() != b.online()) {
                return Boolean.compare(b.online(), a.online());
            }
            return Integer.compare(b.rating(), a.rating());
        });

        return friends;
    }

    @Transactional(readOnly = true)
    public Map<String, List<FriendRequestDto>> getFriendRequests(UUID userId) {
        List<FriendRequestEntity> incoming = friendRequestRepository
                .findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, FriendRequestStatus.PENDING);
        List<FriendRequestEntity> outgoing = friendRequestRepository
                .findBySenderIdAndStatusOrderByCreatedAtDesc(userId, FriendRequestStatus.PENDING);

        List<FriendRequestDto> incomingDtos = incoming.stream().map(this::toRequestDto).toList();
        List<FriendRequestDto> outgoingDtos = outgoing.stream().map(this::toRequestDto).toList();

        return Map.of(
                "incoming", incomingDtos,
                "outgoing", outgoingDtos
        );
    }

    @Transactional
    public FriendRequestDto sendFriendRequest(UUID senderId, UUID receiverId) {
        if (senderId.equals(receiverId)) {
            throw new DevArenaException("You cannot send a friend request to yourself", HttpStatus.BAD_REQUEST, "INVALID_REQUEST");
        }

        rateLimiter.checkLimit("friend-request", senderId, 10);

        if (friendshipRepository.areFriends(senderId, receiverId)) {
            throw new DevArenaException("You are already friends with this player", HttpStatus.BAD_REQUEST, "ALREADY_FRIENDS");
        }

        Optional<FriendRequestEntity> pending = friendRequestRepository.findPendingBetweenUsers(senderId, receiverId, FriendRequestStatus.PENDING);
        if (pending.isPresent()) {
            throw new DevArenaException("A pending friend request already exists between you", HttpStatus.CONFLICT, "REQUEST_PENDING");
        }

        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender user not found"));
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Target player not found"));

        FriendRequestEntity request = new FriendRequestEntity(sender, receiver);
        request = friendRequestRepository.save(request);

        // Send Real-time notification to receiver
        notificationService.createNotification(
                receiver,
                NotificationType.FRIEND_REQUEST,
                "New Friend Request",
                sender.getUsername() + " sent you a friend request.",
                "FRIEND_REQUEST",
                request.getId().toString()
        );

        return toRequestDto(request);
    }

    @Transactional
    public FriendDto acceptFriendRequest(UUID requestId, UUID receiverId) {
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found: " + requestId));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new DevArenaException("Only the request receiver can accept this request", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new DevArenaException("This friend request is no longer pending", HttpStatus.BAD_REQUEST, "INVALID_STATUS");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        // Ensure friendship entity is saved if not already
        FriendshipEntity friendship = friendshipRepository.findFriendshipBetween(request.getSender().getId(), request.getReceiver().getId())
                .orElseGet(() -> friendshipRepository.save(new FriendshipEntity(request.getSender(), request.getReceiver())));

        // Notify sender that their request was accepted
        notificationService.createNotification(
                request.getSender(),
                NotificationType.FRIEND_ACCEPTED,
                "Friend Request Accepted",
                request.getReceiver().getUsername() + " accepted your friend request!",
                "FRIEND",
                request.getReceiver().getId().toString()
        );

        UserEntity other = request.getSender();
        ProfileEntity profile = profileRepository.findByUserId(other.getId()).orElse(null);
        PlayerStatsEntity stats = playerStatsRepository.findByUserId(other.getId()).orElse(null);
        PlayerProgressionEntity progression = playerProgressionRepository.findByUserId(other.getId()).orElse(null);

        return new FriendDto(
                friendship.getId(),
                other.getId(),
                other.getUsername(),
                profile != null ? profile.getDisplayName() : other.getUsername(),
                profile != null ? profile.getAvatar() : "avatar-1",
                progression != null ? progression.getLevel() : 1,
                stats != null ? stats.getRating() : 1000,
                presenceService.isOnline(other.getId()),
                friendship.getCreatedAt()
        );
    }

    @Transactional
    public void rejectFriendRequest(UUID requestId, UUID receiverId) {
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found: " + requestId));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new DevArenaException("Only the request receiver can decline this request", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        FriendshipEntity friendship = friendshipRepository.findFriendshipBetween(userId, friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));

        friendshipRepository.delete(friendship);
    }

    @Transactional
    public FriendBattleInviteDto sendBattleInvite(UUID inviterId, UUID inviteeId, UUID challengeId) {
        if (inviterId.equals(inviteeId)) {
            throw new DevArenaException("Cannot challenge yourself", HttpStatus.BAD_REQUEST, "INVALID_TARGET");
        }

        rateLimiter.checkLimit("battle-invite", inviterId, 10);

        UserEntity inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new ResourceNotFoundException("Inviter not found"));
        UserEntity invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Opponent not found"));

        ChallengeEntity challenge = null;
        if (challengeId != null) {
            challenge = challengeRepository.findById(challengeId).orElse(null);
        }
        if (challenge == null) {
            List<ChallengeEntity> pool = challengeRepository.findAll().stream()
                    .filter(c -> c.getDifficulty() == ChallengeDifficulty.EASY || c.getDifficulty() == ChallengeDifficulty.MEDIUM)
                    .toList();
            challenge = pool.isEmpty() ? challengeRepository.findAll().get(0) : pool.get(new Random().nextInt(pool.size()));
        }

        FriendBattleInviteEntity invite = new FriendBattleInviteEntity(inviter, invitee, challenge);
        invite = battleInviteRepository.save(invite);

        // Push real-time notification to opponent
        notificationService.createNotification(
                invitee,
                NotificationType.BATTLE_INVITE,
                "1v1 Duel Invitation!",
                inviter.getUsername() + " has challenged you to a 1v1 Battle on '" + challenge.getTitle() + "'!",
                "BATTLE_INVITE",
                invite.getId().toString()
        );

        ProfileEntity inviterProfile = profileRepository.findByUserId(inviterId).orElse(null);

        return new FriendBattleInviteDto(
                invite.getId(),
                inviterId,
                inviter.getUsername(),
                inviterProfile != null ? inviterProfile.getDisplayName() : inviter.getUsername(),
                inviterProfile != null ? inviterProfile.getAvatar() : "avatar-1",
                inviteeId,
                invitee.getUsername(),
                challenge.getId(),
                challenge.getTitle(),
                invite.getStatus(),
                null,
                invite.getCreatedAt(),
                invite.getExpiresAt()
        );
    }

    @Transactional
    public Map<String, Object> acceptBattleInvite(UUID inviteId, UUID inviteeId) {
        FriendBattleInviteEntity invite = battleInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle invitation not found: " + inviteId));

        if (!invite.getInvitee().getId().equals(inviteeId)) {
            throw new DevArenaException("Only the challenged player can accept this invite", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        if (invite.getStatus() != BattleInviteStatus.PENDING) {
            throw new DevArenaException("This duel invite is no longer pending", HttpStatus.BAD_REQUEST, "INVALID_STATUS");
        }

        // Create direct battle room
        BattleEntity battle = battleService.createDirectBattle(invite.getInviter(), invite.getInvitee(), invite.getChallenge());

        invite.setStatus(BattleInviteStatus.ACCEPTED);
        invite.setBattle(battle);
        battleInviteRepository.save(invite);

        // Notify inviter via persistent notification
        notificationService.createNotification(
                invite.getInviter(),
                NotificationType.SYSTEM,
                "Duel Accepted!",
                invite.getInvitee().getUsername() + " accepted your challenge! Entering the arena...",
                "BATTLE",
                battle.getId().toString()
        );

        // Broadcast real-time invite accepted event to topic
        Map<String, Object> payload = Map.of(
                "type", "INVITE_ACCEPTED",
                "inviteId", invite.getId().toString(),
                "battleId", battle.getId().toString(),
                "status", "ACCEPTED",
                "challengeTitle", invite.getChallenge().getTitle()
        );
        try {
            messagingTemplate.convertAndSend("/topic/battle-invite." + invite.getId(), payload);
        } catch (Exception e) {
            log.warn("Failed to broadcast invite accepted event: {}", e.getMessage());
        }

        return Map.of(
                "battleId", battle.getId().toString(),
                "status", "ACCEPTED",
                "challengeTitle", invite.getChallenge().getTitle()
        );
    }

    @Transactional(readOnly = true)
    public List<FriendBattleInviteDto> getPendingBattleInvites(UUID userId) {
        return battleInviteRepository.findByInviteeIdAndStatusOrderByCreatedAtDesc(userId, BattleInviteStatus.PENDING)
                .stream()
                .filter(i -> i.getExpiresAt() == null || i.getExpiresAt().isAfter(Instant.now()))
                .map(this::toBattleInviteDto)
                .toList();
    }

    @Transactional
    public void declineBattleInvite(UUID inviteId, UUID inviteeId) {
        FriendBattleInviteEntity invite = battleInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle invitation not found: " + inviteId));

        if (!invite.getInvitee().getId().equals(inviteeId)) {
            throw new DevArenaException("Only the challenged player can decline this invite", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        invite.setStatus(BattleInviteStatus.REJECTED);
        battleInviteRepository.save(invite);

        notificationService.createNotification(
                invite.getInviter(),
                NotificationType.SYSTEM,
                "Duel Declined",
                invite.getInvitee().getUsername() + " declined your 1v1 duel challenge.",
                "FRIENDS",
                invite.getId().toString()
        );

        try {
            messagingTemplate.convertAndSend("/topic/battle-invite." + invite.getId(), Map.of(
                    "type", "INVITE_DECLINED",
                    "inviteId", invite.getId().toString(),
                    "status", "REJECTED"
            ));
        } catch (Exception e) {
            log.warn("Failed to broadcast invite decline: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBattleInviteStatus(UUID inviteId, UUID userId) {
        FriendBattleInviteEntity invite = battleInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Battle invitation not found: " + inviteId));

        return Map.of(
                "inviteId", invite.getId().toString(),
                "status", invite.getStatus().name(),
                "battleId", invite.getBattle() != null ? invite.getBattle().getId().toString() : "",
                "challengeTitle", invite.getChallenge() != null ? invite.getChallenge().getTitle() : ""
        );
    }

    public FriendBattleInviteDto toBattleInviteDto(FriendBattleInviteEntity invite) {
        ProfileEntity inviterProfile = profileRepository.findByUserId(invite.getInviter().getId()).orElse(null);
        return new FriendBattleInviteDto(
                invite.getId(),
                invite.getInviter().getId(),
                invite.getInviter().getUsername(),
                inviterProfile != null ? inviterProfile.getDisplayName() : invite.getInviter().getUsername(),
                inviterProfile != null ? inviterProfile.getAvatar() : "avatar-1",
                invite.getInvitee().getId(),
                invite.getInvitee().getUsername(),
                invite.getChallenge() != null ? invite.getChallenge().getId() : null,
                invite.getChallenge() != null ? invite.getChallenge().getTitle() : "Random Kata",
                invite.getStatus(),
                invite.getBattle() != null ? invite.getBattle().getId() : null,
                invite.getCreatedAt(),
                invite.getExpiresAt()
        );
    }

    private FriendRequestDto toRequestDto(FriendRequestEntity r) {
        ProfileEntity sProfile = profileRepository.findByUserId(r.getSender().getId()).orElse(null);
        PlayerStatsEntity sStats = playerStatsRepository.findByUserId(r.getSender().getId()).orElse(null);
        PlayerProgressionEntity sProg = playerProgressionRepository.findByUserId(r.getSender().getId()).orElse(null);

        return new FriendRequestDto(
                r.getId(),
                r.getSender().getId(),
                r.getSender().getUsername(),
                sProfile != null ? sProfile.getDisplayName() : r.getSender().getUsername(),
                sProfile != null ? sProfile.getAvatar() : "avatar-1",
                sProg != null ? sProg.getLevel() : 1,
                sStats != null ? sStats.getRating() : 1000,
                r.getReceiver().getId(),
                r.getReceiver().getUsername(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
