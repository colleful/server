package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import com.colleful.server.team.domain.Team;
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
public class LeavingTest {

    @InjectMocks
    TeamServiceImpl teamServiceImpl;
    @Mock
    UserServiceForOtherService userService;
    @Mock
    TeamRepository teamRepository;

    User user1;
    User user2;

    Team team1;

    @BeforeEach
    void init() {
        user1 = User.builder().id(1L).teamId(1L).gender(Gender.MALE).build();
        user2 = User.builder().id(2L).teamId(1L).gender(Gender.MALE).build();

        team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .headcount(2)
            .build();
    }

    @Test
    void 팀_탈퇴() {
        given(userService.getUserIfExist(2L)).willReturn(user2);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));

        teamServiceImpl.leaveTeam(2L);

        assertThat(user2.getTeamId()).isNull();
        assertThat(team1.getHeadcount()).isEqualTo(1);
    }

    @Test
    void 리더는_팀_탈퇴_불가() {
        given(userService.getUserIfExist(1L)).willReturn(user1);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));

        Throwable thrown = catchThrowable(() -> teamServiceImpl.leaveTeam(1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
