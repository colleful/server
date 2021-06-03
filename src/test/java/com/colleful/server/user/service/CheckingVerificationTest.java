package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.global.exception.NotVerifiedEmailException;
import com.colleful.server.user.domain.EmailVerification;
import com.colleful.server.user.repository.EmailVerificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CheckingVerificationTest {

    @InjectMocks
    EmailServiceImpl emailService;

    @Mock
    EmailVerificationRepository emailVerificationRepository;

    @Test
    void 이메일이_인증돼있는지_확인() {
        String email = "test@test.com";
        int code = 123456;
        EmailVerification emailVerification = new EmailVerification(email, code);
        emailVerification.check();
        given(emailVerificationRepository.findByEmail(email))
            .willReturn(Optional.of(emailVerification));

        emailService.checkVerification(email);

        then(emailVerificationRepository).should().deleteByEmail(email);
    }

    @Test
    void 이메일이_인증돼있지_않은_경우() {
        String email = "test@test.com";
        int code = 123456;
        EmailVerification emailVerification = new EmailVerification(email, code);
        given(emailVerificationRepository.findByEmail(email))
            .willReturn(Optional.of(emailVerification));

        Throwable thrown = catchThrowable(() -> emailService.checkVerification(email));

        assertThat(thrown).isInstanceOf(NotVerifiedEmailException.class);
    }
}
