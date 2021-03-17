package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.repository.TeamRepository;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserServiceForOtherService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChangingStatusTest {

    @InjectMocks
    TeamServiceImpl teamServiceImpl;
    @Mock
    TeamRepository teamRepository;
    @Mock
    UserServiceForOtherService userService;

    User user1;
    User user2;
    Team team1;

    @BeforeEach
    void init() {
        user1 = User.builder()
            .id(1L)
            .teamId(1L)
            .build();
        user2 = User.builder()
            .id(2L)
            .teamId(1L)
            .build();
        team1 = Team.builder()
            .id(1L)
            .status(TeamStatus.PENDING)
            .leaderId(1L)
            .build();
    }

    @Test
    void 팀_상태_변경() {
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));
        given(userService.getUserIfExist(1L)).willReturn(user1);

        teamServiceImpl.changeStatus(1L, TeamStatus.READY);

        assertThat(team1.getStatus()).isEqualTo(TeamStatus.READY);
    }

    @Test
    void 리더가_아닌_팀_상태_변경_불가() {
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));
        given(userService.getUserIfExist(2L)).willReturn(user2);

        Throwable thrown = catchThrowable(() -> teamServiceImpl.changeStatus(2L, TeamStatus.READY));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    void 준비_상태가_아닌_팀_상태_변경_불가() {
        team1.changeStatus(TeamStatus.READY);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));
        given(userService.getUserIfExist(1L)).willReturn(user1);

        Throwable thrown = catchThrowable(() -> teamServiceImpl.changeStatus(1L, TeamStatus.WATCHING));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
