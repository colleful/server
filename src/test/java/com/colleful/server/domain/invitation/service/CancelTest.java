package com.colleful.server.domain.invitation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.domain.invitation.repository.InvitationRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.user.domain.User;
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
    private InvitationService invitationService;
    @Mock
    private InvitationRepository invitationRepository;

    @Test
    public void 초대_취소() {
        Team team = Team.builder().id(1L).leaderId(1L).build();
        User user = User.builder().id(2L).build();
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        invitationService.cancel(1L, 1L);

        verify(invitationRepository).deleteById(1L);
    }

    @Test
    public void 권한이_없는_사용자가_초대_취소() {
        Team team = Team.builder().id(1L).leaderId(1L).build();
        User user = User.builder().id(2L).build();
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        assertThatThrownBy(() -> invitationService.cancel(1L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
