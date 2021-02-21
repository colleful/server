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
public class CancelTest {

    @InjectMocks
    private InvitationServiceImpl invitationServiceImpl;
    @Mock
    private InvitationRepository invitationRepository;

    private Team team;
    private User user;

    @BeforeEach
    public void init() {
        team = Team.of("test", User.builder().id(1L).gender(Gender.MALE).build());
        user = User.builder().id(2L).gender(Gender.MALE).build();
    }

    @Test
    public void 초대_취소() {
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        invitationServiceImpl.cancel(1L, 1L);

        verify(invitationRepository).deleteById(1L);
    }

    @Test
    public void 권한이_없는_사용자가_초대_취소() {
        when(invitationRepository.findById(1L))
            .thenReturn(Optional.of(new Invitation(team, user)));

        assertThatThrownBy(() -> invitationServiceImpl.cancel(3L, 1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
