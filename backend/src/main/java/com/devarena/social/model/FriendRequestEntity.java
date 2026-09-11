package com.devarena.social.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "friend_requests")
public class FriendRequestEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendRequestStatus status = FriendRequestStatus.PENDING;

    public FriendRequestEntity() {}

    public FriendRequestEntity(UserEntity sender, UserEntity receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = FriendRequestStatus.PENDING;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }
}
