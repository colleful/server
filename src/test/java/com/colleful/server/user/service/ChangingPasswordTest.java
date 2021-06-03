package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class ChangingPasswordTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    UserServiceForOtherService userService;

    @Mock
    EmailServiceForOtherService emailService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void 비밀번호_변경() {
        String email = "test@test.com";
        String password = "password";
        String newPassword = "newPassword";
        User user = User.builder()
            .email(email)
            .password(password)
            .build();
        UserDto.LoginRequest dto = new UserDto.LoginRequest(email, password);
        given(userService.getUserIfExist(email)).willReturn(user);
        willDoNothing().given(emailService).checkVerification(email);
        given(passwordEncoder.encode(password)).willReturn(newPassword);

        authService.changePassword(dto);

        assertThat(user.getPassword()).isEqualTo(newPassword);
    }
}
