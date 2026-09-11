package com.devarena.team.service;

import com.devarena.common.exception.DevArenaException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.notification.model.NotificationType;
import com.devarena.notification.service.NotificationService;
import com.devarena.social.service.PresenceService;
import com.devarena.social.service.SocialRateLimiter;
import com.devarena.team.dto.*;
import com.devarena.team.model.*;
import com.devarena.team.repository.TeamInviteRepository;
import com.devarena.team.repository.TeamMemberRepository;
import com.devarena.team.repository.TeamRepository;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.ProfileEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.PlayerProgressionRepository;
import com.devarena.user.repository.PlayerStatsRepository;
import com.devarena.user.repository.ProfileRepository;
import com.devarena.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final NotificationService notificationService;
    private final PresenceService presenceService;
    private final SocialRateLimiter rateLimiter;

    public TeamService(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            TeamInviteRepository teamInviteRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PlayerStatsRepository playerStatsRepository,
            PlayerProgressionRepository playerProgressionRepository,
            NotificationService notificationService,
            PresenceService presenceService,
            SocialRateLimiter rateLimiter
    ) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamInviteRepository = teamInviteRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.playerProgressionRepository = playerProgressionRepository;
        this.notificationService = notificationService;
        this.presenceService = presenceService;
        this.rateLimiter = rateLimiter;
    }

    @Transactional
    public TeamDto createTeam(UUID ownerId, CreateTeamRequest request) {
        if (teamMemberRepository.findByUserId(ownerId).isPresent()) {
            throw new DevArenaException("You are already a member of a team. Leave your current team first.", HttpStatus.BAD_REQUEST, "ALREADY_IN_TEAM");
        }

        String name = request.name().trim();
        if (teamRepository.existsByNameIgnoreCase(name)) {
            throw new DevArenaException("A team with this name already exists", HttpStatus.CONFLICT, "TEAM_NAME_EXISTS");
        }

        String slug = generateSlug(name);
        if (teamRepository.existsBySlug(slug)) {
            slug = slug + "-" + UUID.randomUUID().toString().substring(0, 4);
        }

        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        TeamEntity team = new TeamEntity(
                name,
                slug,
                request.description(),
                request.avatar(),
                owner,
                request.maxMembers() > 0 ? request.maxMembers() : 10
        );
        team = teamRepository.save(team);

        TeamMemberEntity ownerMember = new TeamMemberEntity(team, owner, TeamRole.OWNER);
        teamMemberRepository.save(ownerMember);

        // Calculate initial team rating based on owner rating
        PlayerStatsEntity stats = playerStatsRepository.findByUserId(ownerId).orElse(null);
        if (stats != null) {
            team.setRating(stats.getRating());
            teamRepository.save(team);
        }

        return getTeam(team.getId());
    }

    @Transactional(readOnly = true)
    public TeamDto getTeam(UUID teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        return toTeamDto(team);
    }

    @Transactional(readOnly = true)
    public TeamDto getMyTeam(UUID userId) {
        TeamMemberEntity membership = teamMemberRepository.findByUserId(userId).orElse(null);
        if (membership == null) {
            return null;
        }
        return toTeamDto(membership.getTeam());
    }

    @Transactional(readOnly = true)
    public List<TeamDto> searchTeams(String query, Pageable pageable) {
        List<TeamEntity> teams = (query == null || query.isBlank())
                ? teamRepository.findAll(pageable).getContent()
                : teamRepository.searchTeams(query.trim(), pageable);

        return teams.stream().map(this::toTeamDto).toList();
    }

    @Transactional
    public TeamInviteDto invitePlayer(UUID teamId, UUID inviterId, UUID inviteeId) {
        rateLimiter.checkLimit("team-invite", inviterId, 10);

        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        TeamMemberEntity inviterMember = teamMemberRepository.findByTeamIdAndUserId(teamId, inviterId)
                .orElseThrow(() -> new DevArenaException("You are not a member of this team", HttpStatus.FORBIDDEN, "NOT_TEAM_MEMBER"));

        if (inviterMember.getRole() == TeamRole.MEMBER) {
            throw new DevArenaException("Only Team Owner or Captain can invite players", HttpStatus.FORBIDDEN, "INSUFFICIENT_PERMISSIONS");
        }

        long currentCount = teamMemberRepository.countByTeamId(teamId);
        if (currentCount >= team.getMaxMembers()) {
            throw new DevArenaException("Team is at maximum capacity (" + team.getMaxMembers() + ")", HttpStatus.BAD_REQUEST, "TEAM_FULL");
        }

        if (teamMemberRepository.findByUserId(inviteeId).isPresent()) {
            throw new DevArenaException("Player is already in a team", HttpStatus.BAD_REQUEST, "PLAYER_ALREADY_IN_TEAM");
        }

        Optional<TeamInviteEntity> pending = teamInviteRepository.findByTeamIdAndInviteeIdAndStatus(teamId, inviteeId, TeamInviteStatus.PENDING);
        if (pending.isPresent()) {
            throw new DevArenaException("Player already has a pending invitation to this team", HttpStatus.CONFLICT, "INVITE_PENDING");
        }

        UserEntity inviter = inviterMember.getUser();
        UserEntity invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitee not found"));

        TeamInviteEntity invite = new TeamInviteEntity(team, inviter, invitee);
        invite = teamInviteRepository.save(invite);

        // Send real-time notification to invitee
        notificationService.createNotification(
                invitee,
                NotificationType.TEAM_INVITE,
                "Team Invitation",
                inviter.getUsername() + " invited you to join team '" + team.getName() + "'!",
                "TEAM_INVITE",
                invite.getId().toString()
        );

        return new TeamInviteDto(
                invite.getId(),
                team.getId(),
                team.getName(),
                team.getAvatar(),
                inviter.getId(),
                inviter.getUsername(),
                invitee.getId(),
                invitee.getUsername(),
                invite.getStatus(),
                invite.getCreatedAt()
        );
    }

    @Transactional
    public TeamDto acceptInvite(UUID inviteId, UUID inviteeId) {
        TeamInviteEntity invite = teamInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found: " + inviteId));

        if (!invite.getInvitee().getId().equals(inviteeId)) {
            throw new DevArenaException("You are not the recipient of this invitation", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        if (invite.getStatus() != TeamInviteStatus.PENDING) {
            throw new DevArenaException("Invitation is no longer pending", HttpStatus.BAD_REQUEST, "INVALID_STATUS");
        }

        if (teamMemberRepository.findByUserId(inviteeId).isPresent()) {
            throw new DevArenaException("You are already in a team. Leave your current team first.", HttpStatus.BAD_REQUEST, "ALREADY_IN_TEAM");
        }

        TeamEntity team = invite.getTeam();
        long currentCount = teamMemberRepository.countByTeamId(team.getId());
        if (currentCount >= team.getMaxMembers()) {
            throw new DevArenaException("Team has reached max capacity", HttpStatus.BAD_REQUEST, "TEAM_FULL");
        }

        invite.setStatus(TeamInviteStatus.ACCEPTED);
        teamInviteRepository.save(invite);

        TeamMemberEntity newMember = new TeamMemberEntity(team, invite.getInvitee(), TeamRole.MEMBER);
        teamMemberRepository.save(newMember);

        // Recalculate average team rating
        recalculateTeamRating(team);

        // Notify Team Owner
        notificationService.createNotification(
                team.getOwner(),
                NotificationType.TEAM_JOINED,
                "New Team Member!",
                invite.getInvitee().getUsername() + " has joined your team '" + team.getName() + "'!",
                "TEAM",
                team.getId().toString()
        );

        return getTeam(team.getId());
    }

    @Transactional
    public void rejectInvite(UUID inviteId, UUID inviteeId) {
        TeamInviteEntity invite = teamInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found: " + inviteId));

        if (!invite.getInvitee().getId().equals(inviteeId)) {
            throw new DevArenaException("You are not the recipient of this invitation", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        invite.setStatus(TeamInviteStatus.REJECTED);
        teamInviteRepository.save(invite);
    }

    @Transactional
    public void removeMember(UUID teamId, UUID actorId, UUID targetMemberId) {
        TeamMemberEntity actor = teamMemberRepository.findByTeamIdAndUserId(teamId, actorId)
                .orElseThrow(() -> new DevArenaException("You are not a member of this team", HttpStatus.FORBIDDEN, "NOT_TEAM_MEMBER"));

        TeamMemberEntity target = teamMemberRepository.findByTeamIdAndUserId(teamId, targetMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Target member not found in team"));

        if (target.getRole() == TeamRole.OWNER) {
            throw new DevArenaException("Cannot remove the team owner", HttpStatus.BAD_REQUEST, "CANNOT_REMOVE_OWNER");
        }

        if (actor.getRole() == TeamRole.MEMBER) {
            throw new DevArenaException("Regular members cannot remove teammates", HttpStatus.FORBIDDEN, "INSUFFICIENT_PERMISSIONS");
        }

        if (actor.getRole() == TeamRole.CAPTAIN && target.getRole() == TeamRole.CAPTAIN) {
            throw new DevArenaException("Captains cannot remove other captains", HttpStatus.FORBIDDEN, "INSUFFICIENT_PERMISSIONS");
        }

        teamMemberRepository.delete(target);
        recalculateTeamRating(actor.getTeam());
    }

    @Transactional
    public void leaveTeam(UUID teamId, UUID userId) {
        TeamMemberEntity member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new DevArenaException("You are not a member of this team", HttpStatus.FORBIDDEN, "NOT_TEAM_MEMBER"));

        TeamEntity team = member.getTeam();

        if (member.getRole() == TeamRole.OWNER) {
            long memberCount = teamMemberRepository.countByTeamId(teamId);
            if (memberCount > 1) {
                // Automatically promote the oldest captain or member to owner
                List<TeamMemberEntity> otherMembers = teamMemberRepository.findByTeamIdOrderByRoleAscJoinedAtAsc(teamId).stream()
                        .filter(m -> !m.getUser().getId().equals(userId))
                        .toList();
                TeamMemberEntity newOwner = otherMembers.get(0);
                newOwner.setRole(TeamRole.OWNER);
                team.setOwner(newOwner.getUser());
                teamMemberRepository.save(newOwner);
                teamRepository.save(team);
            } else {
                // Only member is owner -> dissolve team
                teamMemberRepository.delete(member);
                teamRepository.delete(team);
                return;
            }
        }

        teamMemberRepository.delete(member);
        recalculateTeamRating(team);
    }

    @Transactional(readOnly = true)
    public Page<TeamLeaderboardDto> getTeamLeaderboard(Pageable pageable) {
        Page<TeamEntity> page = teamRepository.findAllByOrderByRatingDescWinsDesc(pageable);
        int startRank = (int) pageable.getOffset() + 1;
        final int[] rankCounter = {startRank};

        return page.map(t -> {
            int total = t.getWins() + t.getLosses();
            double winRate = total > 0 ? Math.round((double) t.getWins() / total * 1000.0) / 10.0 : 0.0;
            return new TeamLeaderboardDto(
                    rankCounter[0]++,
                    t.getId(),
                    t.getName(),
                    t.getSlug(),
                    t.getAvatar(),
                    t.getOwner().getUsername(),
                    (int) teamMemberRepository.countByTeamId(t.getId()),
                    t.getMaxMembers(),
                    t.getRating(),
                    t.getWins(),
                    t.getLosses(),
                    t.getBattles(),
                    winRate
            );
        });
    }

    private void recalculateTeamRating(TeamEntity team) {
        List<TeamMemberEntity> members = teamMemberRepository.findByTeamIdOrderByRoleAscJoinedAtAsc(team.getId());
        if (members.isEmpty()) return;

        int totalRating = 0;
        for (TeamMemberEntity m : members) {
            PlayerStatsEntity stats = playerStatsRepository.findByUserId(m.getUser().getId()).orElse(null);
            totalRating += (stats != null ? stats.getRating() : 1000);
        }
        team.setRating(totalRating / members.size());
        teamRepository.save(team);
    }

    private TeamDto toTeamDto(TeamEntity team) {
        List<TeamMemberEntity> rawMembers = teamMemberRepository.findByTeamIdOrderByRoleAscJoinedAtAsc(team.getId());
        List<TeamMemberDto> memberDtos = new ArrayList<>();

        for (TeamMemberEntity m : rawMembers) {
            UserEntity u = m.getUser();
            ProfileEntity p = profileRepository.findByUserId(u.getId()).orElse(null);
            PlayerStatsEntity s = playerStatsRepository.findByUserId(u.getId()).orElse(null);
            PlayerProgressionEntity pr = playerProgressionRepository.findByUserId(u.getId()).orElse(null);

            memberDtos.add(new TeamMemberDto(
                    m.getId(),
                    u.getId(),
                    u.getUsername(),
                    p != null ? p.getDisplayName() : u.getUsername(),
                    p != null ? p.getAvatar() : "avatar-1",
                    m.getRole(),
                    pr != null ? pr.getLevel() : 1,
                    s != null ? s.getRating() : 1000,
                    presenceService.isOnline(u.getId()),
                    m.getJoinedAt()
            ));
        }

        int totalBattles = team.getWins() + team.getLosses();
        double winRate = totalBattles > 0 ? Math.round((double) team.getWins() / totalBattles * 1000.0) / 10.0 : 0.0;

        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getSlug(),
                team.getDescription(),
                team.getAvatar(),
                team.getOwner().getId(),
                team.getOwner().getUsername(),
                team.getMaxMembers(),
                memberDtos.size(),
                team.getRating(),
                team.getWins(),
                team.getLosses(),
                team.getBattles(),
                winRate,
                memberDtos,
                team.getCreatedAt()
        );
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
