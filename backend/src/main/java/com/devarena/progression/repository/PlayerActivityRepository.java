package com.devarena.progression.repository;

import com.devarena.progression.model.PlayerActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerActivityRepository extends JpaRepository<PlayerActivityEntity, UUID> {

    List<PlayerActivityEntity> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);
}
