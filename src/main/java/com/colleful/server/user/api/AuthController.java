package com.colleful.server.user.api;

import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.dto.UserDto.EmailRequest;
import com.colleful.server.user.dto.UserDto.LoginRequest;
import com.colleful.server.user.dto.UserDto.Request;
import com.colleful.server.user.service.AuthService;
import com.colleful.server.user.service.EmailServiceForController;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailServiceForController emailService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody Request request) {
        User user = authService.join(request);
        return ResponseEntity.created(URI.create("/api/users/" + user.getId()))
            .body(new UserDto.Response(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/join/email")
    public ResponseEntity<?> sendEmailForRegistration(@RequestBody EmailRequest request) {
        emailService.sendEmailForRegistration(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/email")
    public ResponseEntity<?> sendEmailForPassword(@RequestBody EmailRequest request) {
        emailService.sendEmailForPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody LoginRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/check")
    public ResponseEntity<?> check(@RequestBody EmailRequest request) {
        emailService.checkEmail(request);
        return ResponseEntity.ok().build();
    }
}
