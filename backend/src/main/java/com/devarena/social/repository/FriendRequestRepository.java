package com.devarena.social.repository;

import com.devarena.social.model.FriendRequestEntity;
import com.devarena.social.model.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, UUID> {

    List<FriendRequestEntity> findByReceiverIdAndStatusOrderByCreatedAtDesc(UUID receiverId, FriendRequestStatus status);

    List<FriendRequestEntity> findBySenderIdAndStatusOrderByCreatedAtDesc(UUID senderId, FriendRequestStatus status);

    @Query("SELECT fr FROM FriendRequestEntity fr WHERE " +
           "((fr.sender.id = :u1 AND fr.receiver.id = :u2) OR (fr.sender.id = :u2 AND fr.receiver.id = :u1)) " +
           "AND fr.status = :status")
    Optional<FriendRequestEntity> findPendingBetweenUsers(
            @Param("u1") UUID u1,
            @Param("u2") UUID u2,
            @Param("status") FriendRequestStatus status
    );
}
