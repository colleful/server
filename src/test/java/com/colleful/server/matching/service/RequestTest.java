package com.colleful.server.matching.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.matching.repository.MatchingRequestRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.service.TeamServiceForOtherService;
import com.colleful.server.user.domain.Gender;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequestTest {

    @InjectMocks
    MatchingRequestServiceImpl matchingRequestServiceImpl;
    @Mock
    MatchingRequestRepository matchingRequestRepository;
    @Mock
    TeamServiceForOtherService teamService;

    Team team1;
    Team team2;

    @BeforeEach
    void init() {
        team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .status(TeamStatus.PENDING)
            .build();
        team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .gender(Gender.FEMALE)
            .status(TeamStatus.READY)
            .build();
    }

    @Test
    void 매치_요청() {
        given(teamService.getUserTeam(1L)).willReturn(team1);
        given(teamService.getTeamIfExist(2L)).willReturn(team2);
        given(matchingRequestRepository.existsBySentTeamAndReceivedTeam(any(), any()))
            .willReturn(false);

        matchingRequestServiceImpl.request(1L, 2L);

        then(matchingRequestRepository).should().save(any());
    }

    @Test
    void 리더가_아닌_사용자가_매치_요청_불가() {
        given(teamService.getUserTeam(3L)).willReturn(team1);
        given(teamService.getTeamIfExist(2L)).willReturn(team2);

        Throwable thrown = catchThrowable(() -> matchingRequestServiceImpl.request(3L, 2L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    void 이미_요청한_팀에게_다시_요청_불가() {
        given(teamService.getUserTeam(1L)).willReturn(team1);
        given(teamService.getTeamIfExist(2L)).willReturn(team2);
        given(matchingRequestRepository.existsBySentTeamAndReceivedTeam(any(), any()))
            .willReturn(true);

        Throwable thrown = catchThrowable(() -> matchingRequestServiceImpl.request(1L, 2L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
