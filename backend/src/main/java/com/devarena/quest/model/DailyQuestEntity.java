package com.devarena.quest.model;

import com.devarena.common.model.BaseAuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_quests")
public class DailyQuestEntity extends BaseAuditEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_type", nullable = false, length = 64)
    private QuestType questType;

    @Column(name = "target_count", nullable = false)
    private int targetCount = 1;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Column(name = "active_date", nullable = false)
    private LocalDate activeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private QuestStatus status = QuestStatus.ACTIVE;

    public DailyQuestEntity() {}

    public DailyQuestEntity(String title, String description, QuestType questType, int targetCount, int xpReward, LocalDate activeDate) {
        this.title = title;
        this.description = description;
        this.questType = questType;
        this.targetCount = targetCount;
        this.xpReward = xpReward;
        this.activeDate = activeDate;
        this.status = QuestStatus.ACTIVE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public LocalDate getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(LocalDate activeDate) {
        this.activeDate = activeDate;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }
}
