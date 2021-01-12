package com.colleful.server.domain.user.service;

import com.colleful.server.domain.department.service.DepartmentService;
import com.colleful.server.domain.user.domain.EmailVerification;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.domain.user.repository.EmailVerificationRepository;
import com.colleful.server.domain.user.repository.UserRepository;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.InvalidCodeException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.exception.NotMatchedPasswordException;
import com.colleful.server.global.exception.NotVerifiedEmailException;
import com.colleful.server.global.security.JwtProvider;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final DepartmentService departmentService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Value("spring.mail.username")
    private String fromAddress;

    @Override
    public Long join(UserDto.Request dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistResourceException("중복된 이메일입니다.");
        }

        checkVerification(dto.getEmail());

        User user = User.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .nickname(dto.getNickname())
            .birthYear(dto.getBirthYear())
            .gender(Gender.valueOf(dto.getGender()))
            .department(departmentService.getDepartment(dto.getDepartmentId()))
            .selfIntroduction(dto.getSelfIntroduction())
            .roles(Collections.singletonList("ROLE_USER"))
            .build();
        userRepository.save(user);

        return user.getId();
    }

    @Override
    public String login(UserDto.LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return provider.createToken(user.getEmail(), user.getId(), user.getRoles());
    }

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

    @Override
    public void changePassword(UserDto.LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        checkVerification(dto.getEmail());

        user.changePassword(passwordEncoder.encode(dto.getPassword()));
    }

    @Override
    public void checkEmail(UserDto.EmailRequest dto) {
        EmailVerification emailVerification = getEmailVerification(dto.getEmail());

        if (!emailVerification.verify(dto.getCode())) {
            throw new InvalidCodeException("인증번호가 다릅니다.");
        }

        emailVerification.check();
    }

    private void checkVerification(String email) {
        EmailVerification emailVerification = getEmailVerification(email);

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        emailVerificationRepository.deleteById(emailVerification.getId());
    }

    private EmailVerification getEmailVerification(String email) {
        return emailVerificationRepository.findByEmail(email)
            .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));
    }

    private void sendEmail(String email) {
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
}
