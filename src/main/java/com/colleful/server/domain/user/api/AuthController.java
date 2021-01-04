package com.colleful.server.domain.user.api;

import com.colleful.server.domain.user.dto.UserDto.EmailRequest;
import com.colleful.server.domain.user.dto.UserDto.LoginRequest;
import com.colleful.server.domain.user.dto.UserDto.Request;
import com.colleful.server.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody Request request) {
        Long userId = authService.join(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/users/" + userId);
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, token);
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/join/email")
    public ResponseEntity<?> sendEmailForRegistration(@RequestBody EmailRequest request) {
        authService.sendEmailForRegistration(request.getEmail());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/password/email")
    public ResponseEntity<?> sendEmailForPassword(@RequestBody EmailRequest request) {
        authService.sendEmailForPassword(request.getEmail());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody LoginRequest request) {
        authService.changePassword(request);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PatchMapping("/check")
    public ResponseEntity<?> check(@RequestBody EmailRequest request) {
        authService.checkEmail(request);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
