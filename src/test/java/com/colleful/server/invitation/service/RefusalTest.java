package com.colleful.server.invitation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.invitation.domain.Invitation;
import com.colleful.server.invitation.repository.InvitationRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.user.domain.Gender;
import com.colleful.server.user.domain.User;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
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
    private InvitationServiceImpl invitationServiceImpl;
    @Mock
    private InvitationRepository invitationRepository;

    private User user;
    private Team team;

    @BeforeEach
    public void init() {
        user = User.builder().id(2L).gender(Gender.MALE).build();
        team = Team.of("test", User.builder().id(1L).gender(Gender.MALE).build());
    }

    @Test
    public void 초대_거절() {
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        invitationServiceImpl.refuse(2L, 1L);

        verify(invitationRepository).deleteById(1L);
    }

    @Test
    public void 다른_사용자의_초대_거절() {
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        assertThatThrownBy(() -> invitationServiceImpl.refuse(3L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
