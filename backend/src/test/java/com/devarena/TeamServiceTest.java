package com.devarena;

import com.devarena.common.exception.DevArenaException;
import com.devarena.team.dto.CreateTeamRequest;
import com.devarena.team.dto.TeamDto;
import com.devarena.team.dto.TeamInviteDto;
import com.devarena.team.dto.TeamLeaderboardDto;
import com.devarena.team.model.TeamRole;
import com.devarena.team.service.TeamService;
import com.devarena.user.model.UserEntity;
import com.devarena.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should create team, invite member, handle permissions and leave team")
    void testTeamLifecycle() {
        UserEntity owner = userRepository.save(new UserEntity("team_owner", "owner@test.com", "hash"));
        UserEntity player2 = userRepository.save(new UserEntity("team_player2", "player2@test.com", "hash"));

        // 1. Create team
        CreateTeamRequest createReq = new CreateTeamRequest("Apex Coders", "Elite guild", "avatar-apex", 5);
        TeamDto team = teamService.createTeam(owner.getId(), createReq);

        assertThat(team).isNotNull();
        assertThat(team.name()).isEqualTo("Apex Coders");
        assertThat(team.memberCount()).isEqualTo(1);
        assertThat(team.members().get(0).role()).isEqualTo(TeamRole.OWNER);

        // 2. Owner cannot create another team
        assertThatThrownBy(() -> teamService.createTeam(owner.getId(), createReq))
                .isInstanceOf(DevArenaException.class);

        // 3. Invite player 2
        TeamInviteDto invite = teamService.invitePlayer(team.id(), owner.getId(), player2.getId());
        assertThat(invite).isNotNull();
        assertThat(invite.teamName()).isEqualTo("Apex Coders");

        // 4. Player 2 accepts invite
        TeamDto updatedTeam = teamService.acceptInvite(invite.id(), player2.getId());
        assertThat(updatedTeam.memberCount()).isEqualTo(2);

        // 5. Player 2 (regular MEMBER) cannot remove owner
        assertThatThrownBy(() -> teamService.removeMember(team.id(), player2.getId(), owner.getId()))
                .isInstanceOf(DevArenaException.class);

        // 6. Check Team Leaderboard
        Page<TeamLeaderboardDto> leaderboard = teamService.getTeamLeaderboard(PageRequest.of(0, 10));
        assertThat(leaderboard.getContent()).isNotEmpty();
        assertThat(leaderboard.getContent().stream().anyMatch(t -> t.name().equals("Apex Coders"))).isTrue();

        // 7. Player 2 leaves team
        teamService.leaveTeam(team.id(), player2.getId());
        TeamDto teamAfterLeave = teamService.getTeam(team.id());
        assertThat(teamAfterLeave.memberCount()).isEqualTo(1);
    }
}
