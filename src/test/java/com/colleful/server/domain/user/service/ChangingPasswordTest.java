package com.colleful.server.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChangingPasswordTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 비밀번호_변경() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(User.builder().password("password").build()));

        userServiceImpl.changePassword(1L, "new_password");

        User result = userRepository.findById(1L).orElse(User.builder().build());
        assertThat(result.getPassword()).isEqualTo("new_password");
    }
}
