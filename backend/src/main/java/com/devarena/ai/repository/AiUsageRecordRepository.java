package com.devarena.ai.repository;

import com.devarena.ai.model.AiUsageRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AiUsageRecordRepository extends JpaRepository<AiUsageRecordEntity, UUID> {

    long countByUserIdAndCreatedAtAfter(UUID userId, Instant after);

    @Query("SELECT COUNT(a) FROM AiUsageRecordEntity a WHERE a.user.id = :userId AND a.createdAt >= :startOfDay")
    long countTodayUsage(@Param("userId") UUID userId, @Param("startOfDay") Instant startOfDay);

    @Query("SELECT COUNT(a) FROM AiUsageRecordEntity a WHERE a.createdAt >= :after")
    long countSince(@Param("after") Instant after);
}
