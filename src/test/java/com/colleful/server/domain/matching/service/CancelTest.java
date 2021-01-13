package com.colleful.server.domain.matching.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CancelTest {

    @InjectMocks
    private MatchingRequestServiceImpl matchingRequestServiceImpl;
    @Mock
    private MatchingRequestRepository matchingRequestRepository;

    @Test
    public void 매칭_취소() {
        Team team1 = Team.builder().id(1L).matchedTeamId(2L).leaderId(1L).build();
        Team team2 = Team.builder().id(2L).matchedTeamId(1L).leaderId(2L).build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));

        matchingRequestServiceImpl.cancel(1L, 1L);

        verify(matchingRequestRepository).deleteById(1L);
    }

    @Test
    public void 권한이_없는_사용자가_매칭_취소() {
        Team team1 = Team.builder().id(1L).matchedTeamId(2L).leaderId(1L).build();
        Team team2 = Team.builder().id(2L).matchedTeamId(1L).leaderId(2L).build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));

        assertThatThrownBy(() -> matchingRequestServiceImpl.cancel(1L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
