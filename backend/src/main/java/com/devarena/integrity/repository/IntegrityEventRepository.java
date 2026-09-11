package com.devarena.integrity.repository;

import com.devarena.integrity.model.IntegrityEventEntity;
import com.devarena.integrity.model.IntegritySeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrityEventRepository extends JpaRepository<IntegrityEventEntity, UUID> {
    Page<IntegrityEventEntity> findByReviewedOrderByCreatedAtDesc(boolean reviewed, Pageable pageable);

    @Query("SELECT e FROM IntegrityEventEntity e WHERE e.user.id = :userId ORDER BY e.createdAt DESC")
    List<IntegrityEventEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(e.riskScore), 0) FROM IntegrityEventEntity e WHERE e.user.id = :userId AND e.reviewed = false")
    int calculateActiveRiskScore(@Param("userId") UUID userId);

    long countBySeverity(IntegritySeverity severity);
}
