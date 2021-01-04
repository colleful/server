package com.colleful.server.domain.invitation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.domain.invitation.repository.InvitationRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AcceptanceTest {

    @InjectMocks
    private InvitationService invitationService;
    @Mock
    private InvitationRepository invitationRepository;

    @Test
    public void 초대_수락() {
        Team team = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .status(TeamStatus.PENDING)
            .headcount(1)
            .build();
        User user = User.builder()
            .id(2L)
            .gender(Gender.MALE)
            .build();
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        invitationService.accept(1L, 2L);

        assertThat(user.getTeamId()).isEqualTo(1L);
        verify(invitationRepository).deleteById(1L);
    }

    @Test
    public void 다른_사용자의_초대_수락() {
        Team team = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .status(TeamStatus.PENDING)
            .build();
        User user = User.builder()
            .id(2L)
            .gender(Gender.MALE)
            .build();
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        assertThatThrownBy(() -> invitationService.accept(1L, 3L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
