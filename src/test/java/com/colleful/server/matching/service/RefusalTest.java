package com.colleful.server.matching.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.matching.repository.MatchingRequestRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.user.domain.Gender;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RefusalTest {

    @InjectMocks
    private MatchingRequestServiceImpl matchingRequestServiceImpl;
    @Mock
    private MatchingRequestRepository matchingRequestRepository;

    @BeforeEach
    public void init() {
        Team team1 = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .build();
        Team team2 = Team.builder()
            .id(2L)
            .leaderId(2L)
            .gender(Gender.FEMALE)
            .status(TeamStatus.READY)
            .build();
        when(matchingRequestRepository.findById(1L))
            .thenReturn(Optional.of(new MatchingRequest(team1, team2)));
    }

    @Test
    public void 매치_거절() {
        matchingRequestServiceImpl.refuse(2L, 1L);

        verify(matchingRequestRepository).deleteById(1L);
    }

    @Test
    public void 리더가_아닌_사용자가_매치_거절() {
        assertThatThrownBy(() -> matchingRequestServiceImpl.refuse(3L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
