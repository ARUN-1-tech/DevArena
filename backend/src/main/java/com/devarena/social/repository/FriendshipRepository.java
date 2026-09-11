package com.devarena.social.repository;

import com.devarena.social.model.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {

    @Query("SELECT f FROM FriendshipEntity f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<FriendshipEntity> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM FriendshipEntity f WHERE " +
           "(f.user1.id = :u1 AND f.user2.id = :u2) OR (f.user1.id = :u2 AND f.user2.id = :u1)")
    Optional<FriendshipEntity> findFriendshipBetween(@Param("u1") UUID u1, @Param("u2") UUID u2);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM FriendshipEntity f WHERE " +
           "(f.user1.id = :u1 AND f.user2.id = :u2) OR (f.user1.id = :u2 AND f.user2.id = :u1)")
    boolean areFriends(@Param("u1") UUID u1, @Param("u2") UUID u2);
}
