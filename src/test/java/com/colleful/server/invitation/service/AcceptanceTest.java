package com.colleful.server.invitation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import com.colleful.server.invitation.domain.Invitation;
import com.colleful.server.invitation.repository.InvitationRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
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
public class AcceptanceTest {

    @InjectMocks
    InvitationServiceImpl invitationServiceImpl;
    @Mock
    InvitationRepository invitationRepository;

    Team team;
    User user;

    @BeforeEach
    void init() {
        team = Team.builder()
            .id(1L)
            .leaderId(1L)
            .gender(Gender.MALE)
            .status(TeamStatus.PENDING)
            .headcount(1).build();
        user = User.builder().id(2L).gender(Gender.MALE).build();
    }

    @Test
    void 초대_수락() {
        given(invitationRepository.findById(1L))
            .willReturn(Optional.of(new Invitation(team, user)));

        invitationServiceImpl.accept(2L, 1L);

        assertThat(user.getTeamId()).isEqualTo(1L);
        assertThat(team.getHeadcount()).isEqualTo(2);
    }

    @Test
    void 다른_사용자의_초대_수락_불가() {
        given(invitationRepository.findById(1L))
            .willReturn(Optional.of(new Invitation(team, user)));

        Throwable thrown = catchThrowable(() -> invitationServiceImpl.accept(3L, 1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
