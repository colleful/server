package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.colleful.server.team.domain.Team;
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
public class LeavingTest {

    @InjectMocks
    private TeamServiceImpl teamServiceImpl;
    @Mock
    private UserServiceForOtherService userService;
    @Mock
    private TeamRepository teamRepository;

    @BeforeEach
    public void init() {
        when(userService.getUserIfExist(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
    }

    @Test
    public void 팀_탈퇴() {
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(2L)
                .headcount(2)
                .build()));

        teamServiceImpl.removeMember(1L);

        User user = userService.getUserIfExist(1L);
        Team team = teamServiceImpl.getTeamIfExist(1L);
        assertThat(user.getTeamId()).isNull();
        assertThat(team.getHeadcount()).isEqualTo(1);
    }

    @Test
    public void 리더가_팀_탈퇴() {
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(1L)
                .build()));

        assertThatThrownBy(() -> teamServiceImpl.removeMember(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
