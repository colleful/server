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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LeavingTest {

    @InjectMocks
    private TeamService teamService;
    @Mock
    private UserService userService;
    @Mock
    private TeamRepository teamRepository;

    @Test
    public void 팀_탈퇴() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(2L)
                .build()));

        teamService.leaveTeam(1L);

        User user = userService.getUser(1L);
        assertThat(user.getTeamId()).isNull();
    }

    @Test
    public void 리더가_팀_탈퇴() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(1L)
                .build()));

        assertThatThrownBy(() -> teamService.leaveTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 팀_삭제_시_매칭_취소_및_팀_탈퇴() {
        User user1 = User.builder().id(1L).teamId(1L).build();
        User user2 = User.builder().id(2L).teamId(1L).build();
        List<User> members = new ArrayList<>();
        members.add(user1);
        members.add(user2);

        when(userService.getUser(1L))
            .thenReturn(user1);
        when(userService.getUser(2L))
            .thenReturn(user2);
        when(userService.getMembers(1L))
            .thenReturn(members);
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

        Team team = teamService.getTeam(2L);
        User user = userService.getUser(2L);
        assertThat(team.getMatchedTeamId()).isNull();
        assertThat(user.getTeamId()).isNull();
        verify(teamRepository).deleteById(1L);
    }
}
