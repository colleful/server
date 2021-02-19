package com.colleful.server.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
    private TeamServiceImpl teamServiceImpl;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserServiceForOtherService userService;
    private User user1;
    private User user2;

    @BeforeEach
    public void init() {
        this.user1 = User.builder()
            .id(1L)
            .teamId(1L)
            .build();
        this.user2 = User.builder()
            .id(2L)
            .teamId(1L)
            .build();
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(1L)
                .build()));
    }

    @Test
    public void 팀_상태_변경() {
        when(userService.getUserIfExist(1L)).thenReturn(this.user1);

        teamServiceImpl.changeStatus(1L, TeamStatus.READY);

        Team team = teamRepository.findById(1L).orElse(Team.builder().build());
        assertThat(team.getStatus()).isEqualTo(TeamStatus.READY);
    }

    @Test
    public void 리더가_아닌_팀_상태_변경() {
        when(userService.getUserIfExist(2L)).thenReturn(this.user2);

        assertThatThrownBy(() -> teamServiceImpl.changeStatus(2L, TeamStatus.READY))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
