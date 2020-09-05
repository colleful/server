package com.ocupid.server.service;

import com.ocupid.server.domain.EmailVerification;
import com.ocupid.server.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender javaMailSender;
    @Value("spring.mail.username") private String fromAddress;

    public EmailVerificationService(
        EmailVerificationRepository emailVerificationRepository,
        JavaMailSender javaMailSender) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.javaMailSender = javaMailSender;
    }

    public Boolean sendEmail(String email) {
        Integer code = (int) (Math.random() * 900000 + 100000);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(fromAddress);
        simpleMailMessage.setSubject("Colleful 이메일 인증번호입니다.");
        simpleMailMessage.setText("인증번호는 " + code + " 입니다.");

        try {
            EmailVerification emailVerification =
                emailVerificationRepository.findByEmail(email).orElse(null);

            if (emailVerification == null) {
                emailVerification = new EmailVerification();
                emailVerification.setEmail(email);
            }

            emailVerification.setCode(code);
            emailVerificationRepository.save(emailVerification);

            javaMailSender.send(simpleMailMessage);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean check(String email, Integer code) {
        return true;
    }
}
