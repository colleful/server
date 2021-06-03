package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.department.service.DepartmentService;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class JoiningTest {

    @InjectMocks
    AuthServiceImpl authService;
    @Mock
    EmailServiceForOtherService emailService;
    @Mock
    DepartmentService departmentService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    String email;
    UserDto.Request dto;

    @BeforeEach
    void init() {
        email = "test@gmail.com";
        dto = UserDto.Request.builder()
            .email(email)
            .gender("MALE")
            .build();
    }

    @Test
    void 회원가입() {
        given(userRepository.existsByEmail(email)).willReturn(false);

        authService.join(dto);

        then(userRepository).should().save(any());
    }

    @Test
    void 중복된_이메일로_가입_불가() {
        given(userRepository.existsByEmail(email)).willReturn(true);

        Throwable thrown = catchThrowable(() -> authService.join(dto));

        assertThat(thrown).isInstanceOf(AlreadyExistResourceException.class);
    }
}
