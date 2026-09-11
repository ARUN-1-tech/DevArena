package com.devarena.user.repository;

import com.devarena.user.model.PlayerStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStatsEntity, UUID> {
    Optional<PlayerStatsEntity> findByUserId(UUID userId);
}
