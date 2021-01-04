package com.colleful.server.domain.matching.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CheckingRequestTest {

    @InjectMocks
    private MatchingRequestService matchingRequestService;
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

        matchingRequestService.getAllMatchRequests(1L);

        verify(matchingRequestRepository).findAllByReceiver(any());
    }

    @Test
    public void 팀이_없는_사용자가_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).build());

        assertThatThrownBy(() -> matchingRequestService.getAllMatchRequests(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 리더가_아닌_사용자가_내_팀에게_온_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder().id(1L).leaderId(2L).build());

        assertThatThrownBy(() -> matchingRequestService.getAllMatchRequests(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
