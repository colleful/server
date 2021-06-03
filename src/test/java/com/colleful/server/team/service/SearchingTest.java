package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.repository.TeamRepository;
import com.colleful.server.user.domain.Gender;
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
    TeamServiceImpl teamServiceImpl;
    @Mock
    UserServiceForOtherService userService;
    @Mock
    TeamRepository teamRepository;

    User user;
    Team team;

    @BeforeEach
    void init() {
        user = User.builder().id(1L).gender(Gender.MALE).build();
        team = Team.builder()
            .id(1L)
            .status(TeamStatus.PENDING)
            .leaderId(2L)
            .gender(Gender.MALE)
            .headcount(1)
            .build();
    }

    @Test
    void 준비된_팀_정보_조회() {
        team.changeStatus(TeamStatus.READY);
        given(userService.getUserIfExist(1L)).willReturn(user);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team));

        Team team = teamServiceImpl.getTeam(1L, 1L);

        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    void 자기_팀_정보_조회() {
        team.addMember(user);
        given(userService.getUserIfExist(1L)).willReturn(user);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team));

        Team team = teamServiceImpl.getTeam(1L, 1L);

        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    void 속하지_않고_준비되지_않은_팀_정보_조회_불가() {
        given(userService.getUserIfExist(1L)).willReturn(user);
        given(teamRepository.findById(1L)).willReturn(Optional.of(this.team));

        Throwable thrown = catchThrowable(() -> teamServiceImpl.getTeam(1L, 1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    void 유저의_팀_조회() {
        team.addMember(user);
        given(userService.getUserIfExist(1L)).willReturn(user);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team));

        Team team = teamServiceImpl.getUserTeam(1L);

        assertThat(team.getId()).isEqualTo(1L);
    }

    @Test
    void 팀이_없는_유저의_팀_조회_불가() {
        given(userService.getUserIfExist(1L)).willReturn(user);

        Throwable thrown = catchThrowable(() -> teamServiceImpl.getUserTeam(1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
