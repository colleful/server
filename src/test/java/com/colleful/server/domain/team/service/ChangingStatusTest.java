package com.colleful.server.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.repository.TeamRepository;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChangingStatusTest {

    @InjectMocks
    private TeamService teamService;
    @Mock
    private TeamRepository teamRepository;

    @Test
    public void 팀_상태_변경() {
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(1L)
                .build()));

        teamService.updateStatus(1L, 1L, TeamStatus.READY);

        Team team = teamRepository.findById(1L).orElse(Team.builder().build());
        assertThat(team.getStatus()).isEqualTo(TeamStatus.READY);
    }

    @Test
    public void 리더가_아닌_팀_상태_변경() {
        when(teamRepository.findById(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .status(TeamStatus.PENDING)
                .leaderId(1L)
                .build()));

        assertThatThrownBy(() -> teamService.updateStatus(1L, 2L, TeamStatus.READY))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
