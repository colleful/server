package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.user.repository.EmailVerificationRepository;
import com.colleful.server.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class SendingEmailTest {

    @InjectMocks
    EmailServiceImpl emailService;

    @Mock
    EmailVerificationRepository emailVerificationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    JavaMailSender javaMailSender;

    @Test
    void 회원가입용_이메일_전송() {
        String email = "test@test.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        emailService.sendEmailForRegistration(email);

        then(javaMailSender).should().send((SimpleMailMessage) any());
    }

    @Test
    void 이미_가입한_회원은_메일_전송_불가() {
        String email = "test@test.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        Throwable thrown = catchThrowable(() -> emailService.sendEmailForRegistration(email));

        assertThat(thrown).isInstanceOf(AlreadyExistResourceException.class);
    }

    @Test
    void 비밀번호_재설정용_이메일_전송() {
        String email = "test@test.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        emailService.sendEmailForPassword(email);

        then(javaMailSender).should().send((SimpleMailMessage) any());
    }

    @Test
    void 가입하지_않은_회원은_메일_전송_불가() {
        String email = "test@test.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        Throwable thrown = catchThrowable(() -> emailService.sendEmailForPassword(email));

        assertThat(thrown).isInstanceOf(NotFoundResourceException.class);
    }
}
