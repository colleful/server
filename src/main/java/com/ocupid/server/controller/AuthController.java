package com.ocupid.server.controller;

import com.ocupid.server.domain.EmailVerification;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.*;
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
                .orElseThrow(RuntimeException::new);

        if (!emailVerification.getIsChecked()) {
            throw new RuntimeException();
        }

        if (!emailVerificationService.deleteVerificationInfo(emailVerification.getId())) {
            throw new RuntimeException();
        }

        if (!userService.join(user)) {
            throw new RuntimeException();
        }

        return new Response(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = userService.getUserInfo(request.getEmail()).orElseThrow(RuntimeException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }

        String token = provider.createToken(user.getEmail(), user.getId(), user.getRoles());
        return new LoginResponse(token);
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
        if (!emailVerificationService.sendEmail(request.getEmail())) {
            throw new RuntimeException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PatchMapping("/check")
    public ResponseEntity<?> check(@RequestBody EmailRequest request) {
        if (!emailVerificationService.check(request.getEmail(), request.getCode())) {
            throw new RuntimeException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
