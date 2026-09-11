package com.devarena.quest.repository;

import com.devarena.quest.model.DailyQuestEntity;
import com.devarena.quest.model.QuestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DailyQuestRepository extends JpaRepository<DailyQuestEntity, UUID> {

    List<DailyQuestEntity> findByActiveDateAndStatus(LocalDate activeDate, QuestStatus status);

    List<DailyQuestEntity> findByStatus(QuestStatus status);
}
