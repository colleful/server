package com.colleful.server.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.repository.TeamRepository;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;
    @Mock
    private UserService userService;
    @Mock
    private TeamRepository teamRepository;

    @Test
    public void 팀_정보_조회() {
        when(userService.getUserInfo(1L))
            .thenReturn(Optional.of(User.builder().id(1L).teamId(2L).build()));
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(2L)
                .build()));

        assertThatThrownBy(() -> teamService.getTeamInfo(1L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 팀_상태_변경() {
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(1L)
                .build()));

        teamService.updateTeamStatus(1L, 1L, TeamStatus.READY);

        Team team = teamRepository.findById(1L).orElse(Team.builder().build());
        assertThat(team.getStatus()).isEqualTo(TeamStatus.READY);
    }

    @Test
    public void 팀_탈퇴() {
        when(userService.getUserInfo(1L))
            .thenReturn(Optional.of(User.builder().id(1L).teamId(1L).build()));
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(2L)
                .build()));

        teamService.leaveTeam(1L);

        User user = userService.getUserInfo(1L).orElse(User.builder().build());
        assertThat(user.getTeamId()).isNull();
    }

    @Test
    public void 팀_삭제_시_매칭_취소() {
        when(userService.getUserInfo(1L))
            .thenReturn(Optional.of(User.builder().id(1L).teamId(1L).build()));
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.MATCHED)
                .leaderId(1L)
                .matchedTeamId(2L)
                .build()));
        when(teamRepository.findById(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .status(TeamStatus.MATCHED)
                .leaderId(2L)
                .matchedTeamId(1L)
                .build()));

        teamService.deleteTeam(1L);

        Team team = teamRepository.findById(2L).orElse(Team.builder().build());
        assertThat(team.getMatchedTeamId()).isNull();
        verify(teamRepository).deleteById(1L);
    }

    @Test
    public void 매칭() {
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .build()));
        when(teamRepository.findById(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .status(TeamStatus.READY)
                .build()));

        teamService.saveMatchInfo(1L, 2L);

        Team team1 = teamRepository.findById(1L).orElse(Team.builder().build());
        Team team2 = teamRepository.findById(2L).orElse(Team.builder().build());
        assertThat(team1.getMatchedTeamId()).isEqualTo(2L);
        assertThat(team2.getMatchedTeamId()).isEqualTo(1L);
    }
}
