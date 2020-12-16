package com.colleful.server.domain.user.service;

import com.colleful.server.domain.department.service.DepartmentService;
import com.colleful.server.domain.user.domain.EmailVerification;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.InvalidCodeException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.exception.NotMatchedPasswordException;
import com.colleful.server.global.exception.NotVerifiedEmailException;
import com.colleful.server.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final DepartmentService departmentService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(UserDto.Request dto) {
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(dto.getEmail())
                .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        if (userService.isExist(dto.getEmail())) {
            throw new AlreadyExistResourceException("중복된 이메일입니다.");
        }

        emailVerificationService.deleteVerificationInfo(emailVerification.getId());
        userService.join(dto.toEntity(passwordEncoder, departmentService));
    }

    public String login(UserDto.LoginRequest dto) {
        User user = userService.getUserInfoByEmail(dto.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return provider.createToken(user.getEmail(), user.getId(), user.getRoles());
    }

    public void sendEmailForRegistration(String email) {
        if (userService.isExist(email)) {
            throw new AlreadyExistResourceException("이미 가입된 유저입니다.");
        }

        emailVerificationService.sendEmail(email);
    }

    public void sendEmailForPassword(String email) {
        if (!userService.isExist(email)) {
            throw new NotFoundResourceException("가입되지 않은 유저입니다.");
        }

        emailVerificationService.sendEmail(email);
    }

    public void changePassword(UserDto.LoginRequest dto) {
        User user = userService.getUserInfoByEmail(dto.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(dto.getEmail())
                .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        emailVerificationService.deleteVerificationInfo(emailVerification.getId());
        userService.changePassword(user.getId(), dto.getPassword());
    }

    public void checkEmail(UserDto.EmailRequest dto) {
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(dto.getEmail())
                .orElseThrow(() -> new NotFoundResourceException("인증되지 않은 이메일입니다."));

        if (!emailVerification.getCode().equals(dto.getCode())) {
            throw new InvalidCodeException("인증번호가 다릅니다.");
        }

        emailVerification.check();
    }
}
