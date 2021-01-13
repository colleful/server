package com.colleful.server.domain.matching.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AcceptanceTest {

    @InjectMocks
    private MatchingRequestServiceImpl matchingRequestServiceImpl;
    @Mock
    private MatchingRequestRepository matchingRequestRepository;

    @Test
    public void 매치_수락() {
        Team team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .build();
        Team team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));

        matchingRequestServiceImpl.accept(1L, 2L);

        assertThat(team1.getMatchedTeamId()).isEqualTo(2L);
        assertThat(team2.getMatchedTeamId()).isEqualTo(1L);
        assertThat(team1.getStatus()).isEqualTo(TeamStatus.MATCHED);
        assertThat(team2.getStatus()).isEqualTo(TeamStatus.MATCHED);
    }

    @Test
    public void 리더가_아닌_사용자가_매치_수락() {
        Team team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .build();
        Team team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));

        assertThatThrownBy(() -> matchingRequestServiceImpl.accept(1L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
