package com.ocupid.server.controller;

import com.ocupid.server.domain.EmailVerification;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.*;
import com.ocupid.server.exception.AlreadyExistResourceException;
import com.ocupid.server.exception.InvalidCodeException;
import com.ocupid.server.exception.NotFoundResourceException;
import com.ocupid.server.exception.NotMatchedPasswordException;
import com.ocupid.server.exception.NotSentEmailException;
import com.ocupid.server.exception.NotVerifiedEmailException;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.DepartmentService;
import com.ocupid.server.service.EmailVerificationService;
import com.ocupid.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final EmailVerificationService emailVerificationService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
        DepartmentService departmentService,
        EmailVerificationService emailVerificationService,
        JwtProvider provider,
        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.emailVerificationService = emailVerificationService;
        this.provider = provider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/join")
    public Response join(@RequestBody Request request) {
        User user = request.toEntity(passwordEncoder, departmentService);
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(request.getEmail())
                .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));

        // TODO: 이메일, 닉네임 중복 체크

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        if (!emailVerificationService.deleteVerificationInfo(emailVerification.getId())) {
            throw new RuntimeException("다시 시도해 주세요.");
        }

        if (!userService.join(user)) {
            throw new RuntimeException("회원가입에 실패했습니다.");
        }

        return new Response(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = userService.getUserInfo(request.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException("비밀번호가 일치하지 않습니다.");
        }

        String token = provider.createToken(user.getEmail(), user.getId(), user.getRoles());
        return new LoginResponse(token);
    }

    @PostMapping("/join/email")
    public ResponseEntity<?> sendEmailForRegister(@RequestBody EmailRequest request) {
        if (userService.isExist(request.getEmail())) {
            throw new AlreadyExistResourceException("이미 가입된 유저입니다.");
        }

        if (!emailVerificationService.sendEmail(request.getEmail())) {
            throw new NotSentEmailException("이메일이 발송되지 않았습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/password/email")
    public ResponseEntity<?> sendEmailForPassword(@RequestBody EmailRequest request) {
        if (!userService.isExist(request.getEmail())) {
            throw new NotFoundResourceException("가입되지 않은 유저입니다.");
        }

        if (!emailVerificationService.sendEmail(request.getEmail())) {
            throw new NotSentEmailException("이메일이 발송되지 않았습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody LoginRequest request) {
        User user = userService.getUserInfo(request.getEmail())
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        EmailVerification emailVerification =
            emailVerificationService.getEmailVerificationInfo(request.getEmail())
                .orElseThrow(() -> new NotVerifiedEmailException("인증되지 않은 이메일입니다."));

        if (!emailVerification.getIsChecked()) {
            throw new NotVerifiedEmailException("인증되지 않은 이메일입니다.");
        }

        if (!emailVerificationService.deleteVerificationInfo(emailVerification.getId())) {
            throw new RuntimeException("다시 시도해 주세요.");
        }

        if (!userService.changePassword(user, passwordEncoder.encode(request.getPassword()))) {
            throw new RuntimeException("비밀번호 변경에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PatchMapping("/check")
    public ResponseEntity<?> check(@RequestBody EmailRequest request) {
        if (!emailVerificationService.check(request.getEmail(), request.getCode())) {
            throw new InvalidCodeException("인증번호가 다릅니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
