package com.ocupid.server.controller;

import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.*;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtProvider provider;

    public UserController(UserService userService, JwtProvider provider) {
        this.userService = userService;
        this.provider = provider;
    }

    @GetMapping("/{id}")
    public Response getUserInfo(@PathVariable Long id) {
        User user = userService.getUserInfo(id).orElseThrow(RuntimeException::new);
        return new Response(user);
    }

    @PatchMapping("/nickname")
    public Response changeNickname(@RequestHeader("Access-Token") String token,
        @RequestBody Request request) {
        User user = userService.getUserInfo(Long.valueOf((Integer) provider.get(token, "id")))
            .orElseThrow(RuntimeException::new);

        if (!userService.changeNickname(user, request.getNickname())) {
            throw new RuntimeException();
        }

        return new Response(user);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("Access-Token") String token) {
        if (!userService.withdrawal(Long.valueOf((Integer) provider.get(token, "id")))) {
            throw new RuntimeException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
