package com.colleful.server.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
public class SearchingTest {

    @InjectMocks
    private TeamService teamService;
    @Mock
    private UserService userService;
    @Mock
    private TeamRepository teamRepository;

    @Test
    public void 준비된_팀_정보_조회() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.READY)
                .leaderId(2L)
                .build()));

        Team team = teamService.getTeam(1L, 1L);
        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    public void 자기_팀_정보_조회() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(2L)
                .build()));

        Team team = teamService.getTeam(1L, 1L);
        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    public void 속하지_않고_준비되지_않은_팀_정보_조회() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(2L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(2L)
                .build()));

        assertThatThrownBy(() -> teamService.getTeam(1L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
