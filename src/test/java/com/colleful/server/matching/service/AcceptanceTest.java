package com.colleful.server.matching.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.matching.repository.MatchingRequestRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.user.domain.Gender;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AcceptanceTest {

    @InjectMocks
    MatchingRequestServiceImpl matchingRequestServiceImpl;
    @Mock
    MatchingRequestRepository matchingRequestRepository;

    Team team1;
    Team team2;

    @BeforeEach
    void init() {
        team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .build();
        team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .gender(Gender.FEMALE)
            .status(TeamStatus.READY)
            .build();
    }

    @Test
    void 매치_수락() {
        given(matchingRequestRepository.findById(1L))
            .willReturn(Optional.of(new MatchingRequest(team1, team2)));

        matchingRequestServiceImpl.accept(2L, 1L);

        assertThat(team1.getMatchedTeamId()).isEqualTo(2L);
        assertThat(team2.getMatchedTeamId()).isEqualTo(1L);
        assertThat(team1.getStatus()).isEqualTo(TeamStatus.MATCHED);
        assertThat(team2.getStatus()).isEqualTo(TeamStatus.MATCHED);
        then(matchingRequestRepository).should().deleteAllByReceivedTeam(any());
    }

    @Test
    void 리더가_아닌_사용자가_매치_수락_불가() {
        given(matchingRequestRepository.findById(1L))
            .willReturn(Optional.of(new MatchingRequest(team1, team2)));

        Throwable thrown = catchThrowable(() -> matchingRequestServiceImpl.accept(3L, 1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
