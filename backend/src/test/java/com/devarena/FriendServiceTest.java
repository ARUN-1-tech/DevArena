package com.devarena;

import com.devarena.common.exception.DevArenaException;
import com.devarena.social.dto.FriendDto;
import com.devarena.social.dto.FriendRequestDto;
import com.devarena.social.model.FriendRequestStatus;
import com.devarena.social.service.FriendService;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should send friend request, prevent self-request, and accept friend request")
    void testFriendRequestFlow() {
        UserEntity u1 = userRepository.save(new UserEntity("fr_alice", "fr_alice@test.com", "hash"));
        UserEntity u2 = userRepository.save(new UserEntity("fr_bob", "fr_bob@test.com", "hash"));

        // 1. Self friend request should fail
        assertThatThrownBy(() -> friendService.sendFriendRequest(u1.getId(), u1.getId()))
                .isInstanceOf(DevArenaException.class);

        // 2. Send request
        FriendRequestDto req = friendService.sendFriendRequest(u1.getId(), u2.getId());
        assertThat(req).isNotNull();
        assertThat(req.status()).isEqualTo(FriendRequestStatus.PENDING);
        assertThat(req.senderUsername()).isEqualTo("fr_alice");
        assertThat(req.receiverUsername()).isEqualTo("fr_bob");

        // 3. Duplicate request should fail
        assertThatThrownBy(() -> friendService.sendFriendRequest(u1.getId(), u2.getId()))
                .isInstanceOf(DevArenaException.class);

        // 4. Accept request
        FriendDto accepted = friendService.acceptFriendRequest(req.id(), u2.getId());
        assertThat(accepted).isNotNull();
        assertThat(accepted.username()).isEqualTo("fr_alice");

        // 5. Friends list should contain friend
        List<FriendDto> u1Friends = friendService.getFriends(u1.getId());
        assertThat(u1Friends).hasSize(1);
        assertThat(u1Friends.get(0).username()).isEqualTo("fr_bob");

        List<FriendDto> u2Friends = friendService.getFriends(u2.getId());
        assertThat(u2Friends).hasSize(1);
        assertThat(u2Friends.get(0).username()).isEqualTo("fr_alice");

        // 6. Remove friend
        friendService.removeFriend(u1.getId(), u2.getId());
        assertThat(friendService.getFriends(u1.getId())).isEmpty();
        assertThat(friendService.getFriends(u2.getId())).isEmpty();
    }
}
