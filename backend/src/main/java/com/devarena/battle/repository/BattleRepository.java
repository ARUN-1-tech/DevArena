package com.devarena.battle.repository;

import com.devarena.battle.model.BattleEntity;
import com.devarena.battle.model.BattleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BattleRepository extends JpaRepository<BattleEntity, UUID> {

    @Query("""
        SELECT b FROM BattleEntity b
        WHERE (b.player1.id = :userId OR b.player2.id = :userId)
          AND b.status IN (:statuses)
        ORDER BY b.createdAt DESC
    """)
    List<BattleEntity> findActiveBattlesByPlayer(
            @Param("userId") UUID userId,
            @Param("statuses") List<BattleStatus> statuses
    );

    @Query("""
        SELECT b FROM BattleEntity b
        WHERE (b.player1.id = :userId OR b.player2.id = :userId)
          AND b.status = com.devarena.battle.model.BattleStatus.COMPLETED
        ORDER BY b.createdAt DESC
    """)
    Page<BattleEntity> findCompletedBattlesByPlayer(@Param("userId") UUID userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BattleEntity b WHERE b.id = :id")
    Optional<BattleEntity> findByIdWithLock(@Param("id") UUID id);

    @Query("""
        SELECT b FROM BattleEntity b
        WHERE b.status = com.devarena.battle.model.BattleStatus.IN_PROGRESS
          AND b.startedAt IS NOT NULL
          AND b.startedAt < :cutoff
    """)
    List<BattleEntity> findExpiredInProgressBattles(@Param("cutoff") Instant cutoff);
}
