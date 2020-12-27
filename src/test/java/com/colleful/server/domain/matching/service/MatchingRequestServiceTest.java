package com.colleful.server.domain.matching.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.Gender;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchingRequestServiceTest {

    @InjectMocks
    private MatchingRequestService matchingRequestService;
    @Mock
    private MatchingRequestRepository matchingRequestRepository;
    @Mock
    private TeamService teamService;

    @Test
    public void 매치_요청() {
        when(teamService.getTeamInfo(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build()));
        when(teamService.getTeamInfo(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.FEMALE)
                .status(TeamStatus.READY)
                .build()));
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        matchingRequestService.sendMatchRequest(1L, 2L, 1L);

        verify(matchingRequestRepository).save(any());
    }

    @Test
    public void 매치_수락() {
        Team team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .status(TeamStatus.PENDING)
            .build();
        Team team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .gender(Gender.FEMALE)
            .status(TeamStatus.READY)
            .build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));

        matchingRequestService.accept(1L, 2L);

        verify(matchingRequestRepository).deleteById(1L);
    }

    @Test
    public void 매치_거절() {
        Team team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .status(TeamStatus.PENDING)
            .build();
        Team team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .gender(Gender.FEMALE)
            .status(TeamStatus.READY)
            .build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));

        matchingRequestService.refuse(1L, 2L);

        verify(matchingRequestRepository).deleteById(1L);
    }
}
