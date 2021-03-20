package com.colleful.server.user.service;

import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.InvalidCodeException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.exception.NotVerifiedEmailException;
import com.colleful.server.user.domain.EmailVerification;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.repository.EmailVerificationRepository;
import com.colleful.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailServiceForController, EmailServiceForOtherService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;

    @Value("spring.mail.username")
    private String fromAddress;

    @Override
    public void sendEmailForRegistration(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistResourceException("이미 가입된 유저입니다.");
        }

        sendEmail(email);
    }

    @Override
    public void sendEmailForPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new NotFoundResourceException("가입되지 않은 유저입니다.");
        }

        sendEmail(email);
    }

    private void sendEmail(String email) {
        int code = (int) (Math.random() * 900000 + 100000);
        emailVerificationRepository.save(new EmailVerification(email, code));
        javaMailSender.send(createMailMessage(email, code));
    }

    private SimpleMailMessage createMailMessage(String email, int authCode) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(fromAddress);
        simpleMailMessage.setSubject("Colleful 이메일 인증번호입니다.");
        simpleMailMessage.setText("인증번호는 " + authCode + " 입니다.");
        return simpleMailMessage;
    }

    @Override
    public void checkEmail(UserDto.EmailRequest dto) {
        EmailVerification emailVerification = getEmailVerificationIfPresent(dto.getEmail());

        if (!emailVerification.verify(dto.getCode())) {
            throw new InvalidCodeException("인증번호가 다릅니다.");
        }

        emailVerification.check();
        emailVerificationRepository.save(emailVerification);
    }

    @Override
    public void checkVerification(String email) {
        EmailVerification emailVerification = getEmailVerificationIfPresent(email);

        if (emailVerification.isNotChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        emailVerificationRepository.deleteByEmail(email);
    }

    private EmailVerification getEmailVerificationIfPresent(String email) {
        return emailVerificationRepository.findByEmail(email)
            .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));
    }
}
