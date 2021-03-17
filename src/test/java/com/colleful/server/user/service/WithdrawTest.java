package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

import com.colleful.server.user.domain.User;
import com.colleful.server.user.repository.UserRepository;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WithdrawTest {

    @InjectMocks
    UserServiceImpl userServiceImpl;
    @Mock
    UserRepository userRepository;

    @Test
    public void 팀에_속해있는_유저는_회원_탈퇴_불가() {
        given(userRepository.findById(1L))
            .willReturn(Optional.of(User.builder().teamId(1L).build()));

        Throwable thrown = catchThrowable(() -> userServiceImpl.withdrawal(1L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
