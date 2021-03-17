package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.repository.TeamRepository;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserServiceForOtherService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeletingTest {

    @InjectMocks
    TeamServiceImpl teamServiceImpl;
    @Mock
    UserServiceForOtherService userService;
    @Mock
    TeamRepository teamRepository;

    User user1;
    User user2;
    List<User> members;

    Team team1;
    Team team2;

    @BeforeEach
    void init() {
        user1 = User.builder().id(1L).teamId(1L).build();
        user2 = User.builder().id(2L).teamId(1L).build();

        team1 = Team.builder()
            .id(1L)
            .status(TeamStatus.MATCHED)
            .leaderId(1L)
            .matchedTeamId(2L)
            .headcount(2)
            .build();
        team2 = Team.builder()
            .id(2L)
            .status(TeamStatus.MATCHED)
            .leaderId(2L)
            .matchedTeamId(1L)
            .build();

        members = new ArrayList<>();
        members.add(user1);
        members.add(user2);
    }

    @Test
    void 팀_삭제_시_매칭_취소_및_팀_탈퇴() {
        given(userService.getUserIfExist(1L)).willReturn(user1);
        given(userService.getMembers(1L)).willReturn(members);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));
        given(teamRepository.findById(2L)).willReturn(Optional.of(team2));

        teamServiceImpl.deleteTeam(1L);

        assertThat(team2.getMatchedTeamId()).isNull();
        assertThat(team2.getStatus()).isEqualTo(TeamStatus.PENDING);
        assertThat(user2.getTeamId()).isNull();
        verify(teamRepository).deleteById(1L);
    }

    @Test
    void 리더가_아닌_사용자가_팀_삭제_불가() {
        given(userService.getUserIfExist(2L)).willReturn(user2);
        given(teamRepository.findById(1L)).willReturn(Optional.of(team1));

        Throwable thrown = catchThrowable(() -> teamServiceImpl.deleteTeam(2L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
