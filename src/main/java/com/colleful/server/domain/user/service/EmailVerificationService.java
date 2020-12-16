package com.colleful.server.domain.user.service;

import com.colleful.server.domain.user.domain.EmailVerification;
import com.colleful.server.domain.user.repository.EmailVerificationRepository;
import com.colleful.server.global.exception.InvalidCodeException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;
    @Value("spring.mail.username") private String fromAddress;

    public Optional<EmailVerification> getEmailVerificationInfo(String email) {
        return emailVerificationRepository.findByEmail(email);
    }

    @Transactional
    public void sendEmail(String email) {
        Integer code = (int) (Math.random() * 900000 + 100000);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(fromAddress);
        simpleMailMessage.setSubject("Colleful 이메일 인증번호입니다.");
        simpleMailMessage.setText("인증번호는 " + code + " 입니다.");

        EmailVerification emailVerification =
            emailVerificationRepository.findByEmail(email)
                .orElse(new EmailVerification(email, code));

        emailVerification.changeCode(code);
        emailVerificationRepository.save(emailVerification);

        javaMailSender.send(simpleMailMessage);
    }

    public void deleteVerificationInfo(Long id) {
        emailVerificationRepository.deleteById(id);
    }
}
