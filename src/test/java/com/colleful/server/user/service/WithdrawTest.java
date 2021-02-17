package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private UserServiceForControllerImpl userServiceForControllerImpl;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 회원탈퇴() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(User.builder().build()));

        userServiceForControllerImpl.withdrawal(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void 회원탈퇴_불가() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(User.builder().teamId(1L).build()));

        assertThatThrownBy(() -> userServiceForControllerImpl.withdrawal(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
