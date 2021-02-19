package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.repository.TeamRepository;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserServiceForOtherService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SearchingTest {

    @InjectMocks
    private TeamServiceImpl teamServiceImpl;
    @Mock
    private UserServiceForOtherService userService;
    @Mock
    private TeamRepository teamRepository;
    private Team team;

    @BeforeEach
    public void init() {
        this.team = Team.builder()
            .id(1L)
            .status(TeamStatus.PENDING)
            .leaderId(2L)
            .build();
    }

    @Test
    public void 준비된_팀_정보_조회() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(this.team));

        this.team.changeStatus(TeamStatus.READY);
        Team team = teamServiceImpl.getTeam(1L, 1L);

        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    public void 자기_팀_정보_조회() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(this.team));

        Team team = teamServiceImpl.getTeam(1L, 1L);
        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    public void 속하지_않고_준비되지_않은_팀_정보_조회() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).teamId(2L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(this.team));

        assertThatThrownBy(() -> teamServiceImpl.getTeam(1L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 유저의_팀_조회() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(this.team));

        Team team = teamServiceImpl.getUserTeam(1L);
        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    public void 팀이_없는_유저의_팀_조회() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).build());

        assertThatThrownBy(() -> teamServiceImpl.getUserTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
