package com.colleful.server.domain.user.service;

import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.department.service.DepartmentService;
import com.colleful.server.domain.user.domain.EmailVerification;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.domain.user.repository.UserRepository;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.InvalidCodeException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.exception.NotMatchedPasswordException;
import com.colleful.server.global.exception.NotVerifiedEmailException;
import com.colleful.server.global.security.JwtProvider;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final DepartmentService departmentService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    public Long join(UserDto.Request dto) {
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(dto.getEmail())
                .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistResourceException("중복된 이메일입니다.");
        }

        emailVerificationService.deleteVerificationInfo(emailVerification.getId());

        Department department = departmentService.getDepartment(dto.getDepartmentId())
            .orElseThrow(() -> new NotFoundResourceException("학과 정보가 없습니다."));
        User user = User.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .nickname(dto.getNickname())
            .birthYear(dto.getBirthYear())
            .gender(Gender.valueOf(dto.getGender()))
            .department(department)
            .selfIntroduction(dto.getSelfIntroduction())
            .roles(Collections.singletonList("ROLE_USER"))
            .build();
        userRepository.save(user);
        return user.getId();
    }

    public String login(UserDto.LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return provider.createToken(user.getEmail(), user.getId(), user.getRoles());
    }

    public void sendEmailForRegistration(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistResourceException("이미 가입된 유저입니다.");
        }

        emailVerificationService.sendEmail(email);
    }

    public void sendEmailForPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new NotFoundResourceException("가입되지 않은 유저입니다.");
        }

        emailVerificationService.sendEmail(email);
    }

    public void changePassword(UserDto.LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(dto.getEmail())
                .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        emailVerificationService.deleteVerificationInfo(emailVerification.getId());
        user.changePassword(passwordEncoder.encode(dto.getPassword()));
    }

    public void checkEmail(UserDto.EmailRequest dto) {
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(dto.getEmail())
                .orElseThrow(() -> new NotFoundResourceException("인증되지 않은 이메일입니다."));

        if (!emailVerification.verify(dto.getCode())) {
            throw new InvalidCodeException("인증번호가 다릅니다.");
        }

        emailVerification.check();
    }
}
