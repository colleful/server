package com.colleful.server.user.api;

import com.colleful.server.global.security.JwtProperties;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.service.UserServiceForController;
import com.colleful.server.global.security.JwtProvider;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceForController userService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public UserDto.Response getMyInfo(@RequestHeader(JwtProperties.HEADER) String token) {
        User user = userService.getUser(provider.getId(token));
        return new UserDto.Response(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        User user = userService.getUser(id);
        return user.isNotEmpty()
            ? ResponseEntity.ok(new UserDto.Response(user))
            : ResponseEntity.noContent().build();
    }

    @GetMapping("/nickname/{nickname}")
    public List<UserDto.Response> searchUserByNickname(@PathVariable String nickname) {
        List<User> users = userService
            .getUserByNickname(URLDecoder.decode(nickname, StandardCharsets.UTF_8));
        return users.stream().map(UserDto.Response::new).collect(Collectors.toList());
    }

    @PatchMapping
    public ResponseEntity<?> changeUserInfo(@RequestHeader(JwtProperties.HEADER) String token,
        @RequestBody UserDto.Request request) {
        userService.changeUserInfo(provider.getId(token), request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestHeader(JwtProperties.HEADER) String token,
        @RequestBody UserDto.Request request) {
        userService.changePassword(provider.getId(token),
            passwordEncoder.encode(request.getPassword()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader(JwtProperties.HEADER) String token) {
        userService.withdrawal(provider.getId(token));
        return ResponseEntity.ok().build();
    }
}
