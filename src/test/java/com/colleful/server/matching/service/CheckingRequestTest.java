package com.colleful.server.matching.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.matching.repository.MatchingRequestRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.service.TeamService;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CheckingRequestTest {

    @InjectMocks
    private MatchingRequestServiceImpl matchingRequestServiceImpl;
    @Mock
    private MatchingRequestRepository matchingRequestRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private UserService userService;

    @Test
    public void 내_팀에게_온_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder().id(1L).leaderId(1L).build());

        matchingRequestServiceImpl.getAllMatchingRequestsToMyTeam(1L);

        verify(matchingRequestRepository).findAllByReceiver(any());
    }

    @Test
    public void 내_팀이_보낸_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder().id(1L).leaderId(1L).build());

        matchingRequestServiceImpl.getAllMatchingRequestsFromMyTeam(1L);

        verify(matchingRequestRepository).findAllBySender(any());
    }

    @Test
    public void 팀이_없는_사용자가_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).build());

        assertThatThrownBy(() -> matchingRequestServiceImpl.getAllMatchingRequestsToMyTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
        assertThatThrownBy(() -> matchingRequestServiceImpl.getAllMatchingRequestsFromMyTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
