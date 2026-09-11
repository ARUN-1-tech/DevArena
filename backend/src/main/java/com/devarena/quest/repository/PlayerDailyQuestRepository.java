package com.devarena.quest.repository;

import com.devarena.quest.model.PlayerDailyQuestEntity;
import com.devarena.quest.model.PlayerQuestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerDailyQuestRepository extends JpaRepository<PlayerDailyQuestEntity, UUID> {

    List<PlayerDailyQuestEntity> findByUserId(UUID userId);

    Optional<PlayerDailyQuestEntity> findByUserIdAndQuestId(UUID userId, UUID questId);

    Optional<PlayerDailyQuestEntity> findByIdAndUserId(UUID id, UUID userId);

    long countByUserIdAndStatus(UUID userId, PlayerQuestStatus status);
}
