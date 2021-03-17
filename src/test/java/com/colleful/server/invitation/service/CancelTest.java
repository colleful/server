package com.colleful.server.invitation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
    InvitationServiceImpl invitationServiceImpl;
    @Mock
    InvitationRepository invitationRepository;

    Team team;
    User user;

    @BeforeEach
    void init() {
        team = Team.of("test", User.builder().id(1L).gender(Gender.MALE).build());
        user = User.builder().id(2L).gender(Gender.MALE).build();
    }

    @Test
    void 초대_취소() {
        given(invitationRepository.findById(1L))
            .willReturn(Optional.of(new Invitation(team, user)));

        invitationServiceImpl.cancel(1L, 1L);

        verify(invitationRepository).deleteById(1L);
    }

    @Test
    void 권한이_없는_사용자가_초대_취소_불가() {
        given(invitationRepository.findById(1L))
            .willReturn(Optional.of(new Invitation(team, user)));

        Throwable thrown = catchThrowable(() -> invitationServiceImpl.cancel(3L, 1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
