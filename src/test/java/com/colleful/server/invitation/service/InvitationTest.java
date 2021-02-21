package com.colleful.server.invitation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.invitation.repository.InvitationRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
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
    private InvitationServiceImpl invitationServiceImpl;
    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private TeamServiceForOtherService teamService;
    @Mock
    private UserServiceForOtherService userService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void init() {
        this.user1 = User.builder().id(1L).gender(Gender.MALE).build();
        this.user2 = User.builder().id(2L).gender(Gender.MALE).build();
        this.user3 = User.builder().id(3L).gender(Gender.MALE).build();
    }

    @Test
    public void 초대() {
        when(teamService.getUserTeam(1L)).thenReturn(Team.of("test", user1));
        when(userService.getUserIfExist(2L)).thenReturn(user2);

        invitationServiceImpl.invite(1L, 2L);

        verify(invitationRepository).save(any());
    }

    @Test
    public void 리더가_아닌_사용자가_초대() {
        when(teamService.getUserTeam(1L)).thenReturn(Team.of("test", user3));

        assertThatThrownBy(() -> invitationServiceImpl.invite(1L, 2L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
