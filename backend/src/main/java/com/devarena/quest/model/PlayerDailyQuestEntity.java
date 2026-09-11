package com.devarena.quest.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "player_daily_quests", uniqueConstraints = {
        @UniqueConstraint(name = "uq_player_daily_quests", columnNames = {"user_id", "quest_id"})
})
public class PlayerDailyQuestEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private DailyQuestEntity quest;

    @Column(name = "current_count", nullable = false)
    private int currentCount = 0;

    @Column(name = "target_count", nullable = false)
    private int targetCount = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private PlayerQuestStatus status = PlayerQuestStatus.IN_PROGRESS;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    public PlayerDailyQuestEntity() {}

    public PlayerDailyQuestEntity(UserEntity user, DailyQuestEntity quest, int currentCount, int targetCount, PlayerQuestStatus status) {
        this.user = user;
        this.quest = quest;
        this.currentCount = currentCount;
        this.targetCount = targetCount;
        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public DailyQuestEntity getQuest() {
        return quest;
    }

    public void setQuest(DailyQuestEntity quest) {
        this.quest = quest;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
        if (this.currentCount >= this.targetCount && this.status == PlayerQuestStatus.IN_PROGRESS) {
            this.status = PlayerQuestStatus.COMPLETED;
            this.completedAt = Instant.now();
        }
    }

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public PlayerQuestStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerQuestStatus status) {
        this.status = status;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Instant claimedAt) {
        this.claimedAt = claimedAt;
    }
}
