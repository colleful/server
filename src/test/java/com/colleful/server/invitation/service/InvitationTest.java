package com.colleful.server.invitation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.colleful.server.invitation.repository.InvitationRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.service.TeamServiceForOtherService;
import com.colleful.server.user.domain.Gender;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserServiceForOtherService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvitationTest {

    @InjectMocks
    InvitationServiceImpl invitationServiceImpl;
    @Mock
    InvitationRepository invitationRepository;
    @Mock
    TeamServiceForOtherService teamService;
    @Mock
    UserServiceForOtherService userService;

    User user1;
    User user2;

    @BeforeEach
    void init() {
        user1 = User.builder().id(1L).gender(Gender.MALE).build();
        user2 = User.builder().id(2L).gender(Gender.MALE).build();
    }

    @Test
    void 초대() {
        given(teamService.getUserTeam(1L)).willReturn(Team.of("test", user1));
        given(userService.getUserIfExist(2L)).willReturn(user2);
        given(invitationRepository.existsByTeamAndUser(any(), any())).willReturn(false);

        invitationServiceImpl.invite(1L, 2L);

        verify(invitationRepository).save(any());
    }

    @Test
    void 리더가_아닌_사용자가_초대_불가() {
        given(teamService.getUserTeam(3L)).willReturn(Team.of("test", user1));
        given(userService.getUserIfExist(2L)).willReturn(user2);

        Throwable thrown = catchThrowable(() -> invitationServiceImpl.invite(3L, 2L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    void 이미_초대한_유저_다시_초대_불가() {
        given(teamService.getUserTeam(1L)).willReturn(Team.of("test", user1));
        given(userService.getUserIfExist(2L)).willReturn(user2);
        given(invitationRepository.existsByTeamAndUser(any(), any())).willReturn(true);

        Throwable thrown = catchThrowable(() -> invitationServiceImpl.invite(1L, 2L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
