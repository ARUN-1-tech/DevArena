package com.devarena.social.repository;

import com.devarena.social.model.BattleInviteStatus;
import com.devarena.social.model.FriendBattleInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendBattleInviteRepository extends JpaRepository<FriendBattleInviteEntity, UUID> {

    List<FriendBattleInviteEntity> findByInviteeIdAndStatusOrderByCreatedAtDesc(UUID inviteeId, BattleInviteStatus status);

    Optional<FriendBattleInviteEntity> findByInviterIdAndInviteeIdAndStatus(UUID inviterId, UUID inviteeId, BattleInviteStatus status);
}
