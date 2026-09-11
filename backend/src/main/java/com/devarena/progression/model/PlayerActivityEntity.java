package com.devarena.progression.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "player_activities")
public class PlayerActivityEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 64)
    private ActivityType activityType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "xp_earned", nullable = false)
    private int xpEarned = 0;

    public PlayerActivityEntity() {}

    public PlayerActivityEntity(UserEntity user, ActivityType activityType, String title, String description, int xpEarned) {
        this.user = user;
        this.activityType = activityType;
        this.title = title;
        this.description = description;
        this.xpEarned = xpEarned;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
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

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }
}
