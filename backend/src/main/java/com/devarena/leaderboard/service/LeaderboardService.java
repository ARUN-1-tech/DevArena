package com.devarena.leaderboard.service;

import com.devarena.leaderboard.dto.LeaderboardItemDto;
import com.devarena.leaderboard.dto.LeaderboardResponse;
import com.devarena.user.model.PlayerProgressionEntity;
import com.devarena.user.model.PlayerStatsEntity;
import com.devarena.user.model.ProfileEntity;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LeaderboardService {

    private final EntityManager entityManager;
    private final UserRepository userRepository;

    public LeaderboardService(EntityManager entityManager, UserRepository userRepository) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderboard(String typeStr, int page, int size, UUID currentUserId) {
        String type = typeStr != null ? typeStr.toUpperCase() : "GLOBAL";
        if (!List.of("GLOBAL", "WEEKLY", "MONTHLY").contains(type)) {
            type = "GLOBAL";
        }

        page = Math.max(0, page);
        size = Math.min(100, Math.max(1, size));

        // Deterministic ordering: rating DESC, challengesSolved DESC, totalXp DESC, username ASC
        String baseJpql = "SELECT u FROM UserEntity u " +
                "LEFT JOIN FETCH u.profile pr " +
                "LEFT JOIN FETCH u.stats st " +
                "LEFT JOIN FETCH u.progression pg ";

        // Filter out dummy/test accounts
        String baseFilter = "WHERE LOWER(u.username) NOT LIKE 'tester_%' AND LOWER(u.username) NOT LIKE 'testuser_%' ";

        String whereClause = baseFilter;
        Map<String, Object> params = new HashMap<>();

        if ("WEEKLY".equals(type)) {
            Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
            whereClause = baseFilter + "AND EXISTS (SELECT 1 FROM PlayerActivityEntity a WHERE a.user = u AND a.createdAt >= :cutoff) ";
            params.put("cutoff", weekAgo);
        } else if ("MONTHLY".equals(type)) {
            Instant monthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
            whereClause = baseFilter + "AND EXISTS (SELECT 1 FROM PlayerActivityEntity a WHERE a.user = u AND a.createdAt >= :cutoff) ";
            params.put("cutoff", monthAgo);
        }

        String orderClause = "ORDER BY COALESCE(st.rating, 1000) DESC, " +
                "COALESCE(pg.challengesSolved, 0) DESC, " +
                "COALESCE(pg.totalXp, 0) DESC, " +
                "u.username ASC";

        // Count total elements
        String countJpql = "SELECT COUNT(u) FROM UserEntity u " + whereClause;
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        params.forEach(countQuery::setParameter);
        long totalElements = countQuery.getSingleResult();

        // If weekly/monthly has no active users, fall back to global users so the leaderboard is never blank
        if (totalElements == 0 && !"GLOBAL".equals(type)) {
            whereClause = baseFilter;
            params.clear();
            countQuery = entityManager.createQuery("SELECT COUNT(u) FROM UserEntity u " + baseFilter, Long.class);
            totalElements = countQuery.getSingleResult();
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Fetch paginated rankings
        TypedQuery<UserEntity> query = entityManager.createQuery(baseJpql + whereClause + orderClause, UserEntity.class);
        params.forEach(query::setParameter);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<UserEntity> users = query.getResultList();

        List<LeaderboardItemDto> rankings = new ArrayList<>();
        int currentRank = page * size + 1;
        for (UserEntity u : users) {
            rankings.add(mapToDto(u, currentRank++));
        }

        // Fetch top 3 for the podium
        List<LeaderboardItemDto> top3 = new ArrayList<>();
        if (page == 0 && rankings.size() >= 1) {
            top3 = rankings.subList(0, Math.min(3, rankings.size()));
        } else {
            TypedQuery<UserEntity> top3Query = entityManager.createQuery(baseJpql + whereClause + orderClause, UserEntity.class);
            params.forEach(top3Query::setParameter);
            top3Query.setFirstResult(0);
            top3Query.setMaxResults(3);
            List<UserEntity> top3Users = top3Query.getResultList();
            int r = 1;
            for (UserEntity u : top3Users) {
                top3.add(mapToDto(u, r++));
            }
        }

        // Find current player's rank if requested
        LeaderboardItemDto myRankDto = null;
        if (currentUserId != null) {
            myRankDto = findUserRank(currentUserId, whereClause, orderClause, params);
        }

        return new LeaderboardResponse(
                type,
                page,
                size,
                totalElements,
                totalPages,
                top3,
                rankings,
                myRankDto
        );
    }

    private LeaderboardItemDto findUserRank(UUID userId, String whereClause, String orderClause, Map<String, Object> params) {
        UserEntity targetUser = userRepository.findById(userId).orElse(null);
        if (targetUser == null) return null;

        PlayerStatsEntity stats = targetUser.getStats();
        PlayerProgressionEntity prog = targetUser.getProgression();
        int rating = stats != null ? stats.getRating() : 1000;
        int solved = prog != null ? prog.getChallengesSolved() : 0;
        int xp = prog != null ? prog.getTotalXp() : 0;
        String username = targetUser.getUsername();

        // Calculate count of players ranked higher
        String rankJpql = "SELECT COUNT(u) FROM UserEntity u " +
                "LEFT JOIN u.stats st " +
                "LEFT JOIN u.progression pg " +
                (whereClause.isEmpty() ? "WHERE " : whereClause + " AND ") +
                "(COALESCE(st.rating, 1000) > :rating OR " +
                "(COALESCE(st.rating, 1000) = :rating AND COALESCE(pg.challengesSolved, 0) > :solved) OR " +
                "(COALESCE(st.rating, 1000) = :rating AND COALESCE(pg.challengesSolved, 0) = :solved AND COALESCE(pg.totalXp, 0) > :xp) OR " +
                "(COALESCE(st.rating, 1000) = :rating AND COALESCE(pg.challengesSolved, 0) = :solved AND COALESCE(pg.totalXp, 0) = :xp AND u.username < :username))";

        TypedQuery<Long> rankQuery = entityManager.createQuery(rankJpql, Long.class);
        params.forEach(rankQuery::setParameter);
        rankQuery.setParameter("rating", rating);
        rankQuery.setParameter("solved", solved);
        rankQuery.setParameter("xp", xp);
        rankQuery.setParameter("username", username);

        int rank = rankQuery.getSingleResult().intValue() + 1;
        return mapToDto(targetUser, rank);
    }

    private LeaderboardItemDto mapToDto(UserEntity u, int rank) {
        ProfileEntity profile = u.getProfile();
        PlayerStatsEntity stats = u.getStats();
        PlayerProgressionEntity prog = u.getProgression();

        String displayName = profile != null ? profile.getDisplayName() : u.getUsername();
        String avatar = profile != null ? profile.getAvatar() : "avatar-1";
        int level = prog != null ? prog.getLevel() : 1;
        int totalXp = prog != null ? prog.getTotalXp() : 0;
        int solved = prog != null ? prog.getChallengesSolved() : 0;

        int rating = stats != null ? stats.getRating() : 1000;
        int wins = stats != null ? stats.getWins() : 0;
        int losses = stats != null ? stats.getLosses() : 0;

        double winRate = (wins + losses > 0)
                ? Math.round(((double) wins / (wins + losses)) * 1000.0) / 10.0
                : 0.0;

        return new LeaderboardItemDto(
                rank,
                u.getId(),
                u.getUsername(),
                displayName,
                avatar,
                level,
                rating,
                totalXp,
                wins,
                losses,
                solved,
                winRate
        );
    }
}
