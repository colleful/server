package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.repository.TeamRepository;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserServiceForOtherService;
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
public class DeletingTest {

    @InjectMocks
    private TeamServiceImpl teamServiceImpl;
    @Mock
    private UserServiceForOtherService userService;
    @Mock
    private TeamRepository teamRepository;

    @Test
    public void 팀_삭제_시_매칭_취소_및_팀_탈퇴() {
        User user1 = User.builder().id(1L).teamId(1L).build();
        User user2 = User.builder().id(2L).teamId(1L).build();
        List<User> members = new ArrayList<>();
        members.add(user1);
        members.add(user2);

        when(userService.getUserIfExist(1L))
            .thenReturn(user1);
        when(userService.getUserIfExist(2L))
            .thenReturn(user2);
        when(userService.getMembers(1L))
            .thenReturn(members);
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.MATCHED)
                .leaderId(1L)
                .matchedTeamId(2L)
                .headcount(2)
                .build()));
        when(teamRepository.findById(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .status(TeamStatus.MATCHED)
                .leaderId(2L)
                .matchedTeamId(1L)
                .build()));

        teamServiceImpl.deleteTeam(1L);

        Team team = teamServiceImpl.getTeamIfExist(2L);
        User user = userService.getUserIfExist(2L);
        assertThat(team.getMatchedTeamId()).isNull();
        assertThat(team.getStatus()).isEqualTo(TeamStatus.PENDING);
        assertThat(user.getTeamId()).isNull();
        verify(teamRepository).deleteById(1L);
    }

    @Test
    public void 팀이_없는_사용자가_팀_삭제() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).build());

        assertThatThrownBy(() -> teamServiceImpl.deleteTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 리더가_아닌_사용자가_팀_삭제() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(2L)
                .build()));

        assertThatThrownBy(() -> teamServiceImpl.deleteTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
