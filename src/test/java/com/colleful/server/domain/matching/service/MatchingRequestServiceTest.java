package com.colleful.server.domain.matching.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
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
    @Mock
    private UserService userService;

    @Test
    public void 매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build()));
        when(teamService.getTeam(2L))
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
    public void 같은_성별_팀에게_매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build()));
        when(teamService.getTeam(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.MALE)
                .status(TeamStatus.READY)
                .build()));
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> matchingRequestService.sendMatchRequest(1L, 2L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 리더가_아닌_사용자가_매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build()));
        when(teamService.getTeam(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.FEMALE)
                .status(TeamStatus.READY)
                .build()));
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> matchingRequestService.sendMatchRequest(1L, 2L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 준비가_안_된_팀에게_매치_요청() {
        when(teamService.getTeam(1L))
            .thenReturn(Optional.of(Team.builder()
                .id(1L)
                .leaderId(1L)
                .gender(Gender.MALE)
                .status(TeamStatus.PENDING)
                .build()));
        when(teamService.getTeam(2L))
            .thenReturn(Optional.of(Team.builder()
                .id(2L)
                .leaderId(2L)
                .gender(Gender.FEMALE)
                .status(TeamStatus.PENDING)
                .build()));
        when(matchingRequestRepository.existsBySenderAndReceiver(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> matchingRequestService.sendMatchRequest(1L, 2L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 내_팀에게_온_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(Optional.of(User.builder().id(1L).teamId(1L).build()));
        when(teamService.getTeam(1L))
            .thenReturn(Optional.of(Team.builder().id(1L).leaderId(1L).build()));

        matchingRequestService.getAllMatchRequests(1L);

        verify(matchingRequestRepository).findAllByReceiver(any());
    }

    @Test
    public void 팀이_없는_사용자가_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(Optional.of(User.builder().id(1L).build()));

        assertThatThrownBy(() -> matchingRequestService.getAllMatchRequests(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 리더가_아닌_사용자가_내_팀에게_온_모든_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(Optional.of(User.builder().id(1L).teamId(1L).build()));
        when(teamService.getTeam(1L))
            .thenReturn(Optional.of(Team.builder().id(1L).leaderId(2L).build()));

        assertThatThrownBy(() -> matchingRequestService.getAllMatchRequests(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

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

        matchingRequestService.accept(1L, 2L);

        assertThat(team1.getMatchedTeamId()).isEqualTo(2L);
        assertThat(team2.getMatchedTeamId()).isEqualTo(1L);
        verify(matchingRequestRepository).deleteById(1L);
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

        assertThatThrownBy(() -> matchingRequestService.accept(1L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    public void 매치_거절() {
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

        matchingRequestService.refuse(1L, 2L);

        verify(matchingRequestRepository).deleteById(1L);
    }

    @Test
    public void 리더가_아닌_사용자가_매치_거절() {
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

        assertThatThrownBy(() -> matchingRequestService.refuse(1L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
