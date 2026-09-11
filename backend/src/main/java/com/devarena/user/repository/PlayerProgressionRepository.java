package com.devarena.user.repository;

import com.devarena.user.model.PlayerProgressionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerProgressionRepository extends JpaRepository<PlayerProgressionEntity, UUID> {
    Optional<PlayerProgressionEntity> findByUserId(UUID userId);
}
