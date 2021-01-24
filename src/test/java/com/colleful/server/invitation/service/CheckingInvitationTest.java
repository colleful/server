package com.colleful.server.invitation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.invitation.repository.InvitationRepository;
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
public class CheckingInvitationTest {

    @InjectMocks
    private InvitationServiceImpl invitationServiceImpl;
    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private UserService userService;

    @Test
    public void 나에게_온_모든_초대_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).build());

        invitationServiceImpl.getAllInvitationsToMe(1L);

        verify(invitationRepository).findAllByUser(any());
    }

    @Test
    public void 내가_초대한_모든_사용자_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).teamId(1L).build());
        when(teamService.getTeam(1L))
            .thenReturn(Team.builder().id(1L).build());

        invitationServiceImpl.getAllInvitationsFromMyTeam(1L);

        verify(invitationRepository).findAllByTeam(any());
    }

    @Test
    public void 팀이_없는_사용자가_보낸_요청_확인() {
        when(userService.getUser(1L))
            .thenReturn(User.builder().id(1L).build());

        assertThatThrownBy(() -> invitationServiceImpl.getAllInvitationsFromMyTeam(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
