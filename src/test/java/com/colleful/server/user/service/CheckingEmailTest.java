package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.colleful.server.global.exception.InvalidCodeException;
import com.colleful.server.user.domain.EmailVerification;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.repository.EmailVerificationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CheckingEmailTest {

    @InjectMocks
    EmailServiceImpl emailService;

    @Mock
    EmailVerificationRepository emailVerificationRepository;

    @Test
    void 이메일_인증번호_확인() {
        String email = "test@test.com";
        int code = 123456;
        UserDto.EmailRequest dto = new UserDto.EmailRequest(email, code);
        given(emailVerificationRepository.findByEmail(email))
            .willReturn(Optional.of(new EmailVerification(email, code)));

        emailService.checkEmail(dto);

        then(emailVerificationRepository).should().save(any());
    }

    @Test
    void 인증번호가_다를_경우_확인_불가() {
        String email = "test@test.com";
        int code = 123456;
        int anotherCode = 123457;
        UserDto.EmailRequest dto = new UserDto.EmailRequest(email, code);
        given(emailVerificationRepository.findByEmail(email))
            .willReturn(Optional.of(new EmailVerification(email, anotherCode)));

        Throwable thrown = catchThrowable(() -> emailService.checkEmail(dto));

        assertThat(thrown).isInstanceOf(InvalidCodeException.class);
    }
}
