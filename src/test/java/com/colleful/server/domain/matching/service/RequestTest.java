package com.colleful.server.domain.matching.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequestTest {

    @InjectMocks
    private MatchingRequestService matchingRequestService;
    @Mock
    private MatchingRequestRepository matchingRequestRepository;
    @Mock
    private TeamService teamService;

    @Test
    public void 매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build());
        when(teamService.getTeam(2L))
            .thenReturn(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.FEMALE)
                .status(TeamStatus.READY)
                .build());
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        matchingRequestService.request(1L, 2L, 1L);

        verify(matchingRequestRepository).save(any());
    }

    @Test
    public void 같은_성별_팀에게_매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build());
        when(teamService.getTeam(2L))
            .thenReturn(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.MALE)
                .status(TeamStatus.READY)
                .build());
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> matchingRequestService.request(1L, 2L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 리더가_아닌_사용자가_매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build());
        when(teamService.getTeam(2L))
            .thenReturn(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.FEMALE)
                .status(TeamStatus.READY)
                .build());
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> matchingRequestService.request(1L, 2L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 준비가_안_된_팀에게_매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build());
        when(teamService.getTeam(2L))
            .thenReturn(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.FEMALE)
                .status(TeamStatus.PENDING)
                .build());
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> matchingRequestService.request(1L, 2L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}