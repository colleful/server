package com.ocupid.server.controller;

import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.*;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtProvider provider,
        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.provider = provider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/join")
    public Response join(@RequestBody Request request) {
        User user = request.toEntity(passwordEncoder);

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
}
