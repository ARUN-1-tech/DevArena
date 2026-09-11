package com.devarena.social.model;

import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(name = "uq_friendship_pair", columnNames = {"user1_id", "user2_id"})
})
public class FriendshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private UserEntity user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private UserEntity user2;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public FriendshipEntity() {}

    public FriendshipEntity(UserEntity u1, UserEntity u2) {
        // Enforce consistent ordering: user1 has lexicographically / numerically lower id
        if (u1.getId().compareTo(u2.getId()) < 0) {
            this.user1 = u1;
            this.user2 = u2;
        } else {
            this.user1 = u2;
            this.user2 = u1;
        }
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser1() {
        return user1;
    }

    public void setUser1(UserEntity user1) {
        this.user1 = user1;
    }

    public UserEntity getUser2() {
        return user2;
    }

    public void setUser2(UserEntity user2) {
        this.user2 = user2;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
