package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.global.exception.NotMatchedPasswordException;
import com.colleful.server.global.security.JwtProvider;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class loginTest {

    @InjectMocks
    AuthServiceImpl authService;
    @Mock
    UserServiceForOtherService userService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtProvider provider;

    UserDto.LoginRequest dto;

    @BeforeEach
    void init() {
        dto = new UserDto.LoginRequest();
    }

    @Test
    void 로그인() {
        given(userService.getUserIfExist(nullable(String.class)))
            .willReturn(User.getEmptyInstance());
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        authService.login(dto);

        then(provider).should().createToken(any(), any(), any());
    }

    @Test
    void 비밀번호_불일치() {
        given(userService.getUserIfExist(nullable(String.class)))
            .willReturn(User.getEmptyInstance());
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        Throwable thrown = catchThrowable(() -> authService.login(dto));

        assertThat(thrown).isInstanceOf(NotMatchedPasswordException.class);
    }
}
