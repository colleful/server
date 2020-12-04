package com.colleful.server.domain.user;

import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.security.JwtProvider;
import com.colleful.server.domain.department.DepartmentService;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
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
public class UserController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
        DepartmentService departmentService,
        JwtProvider provider,
        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.provider = provider;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public UserDto.Response getMyInfo(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        return new UserDto.Response(user);
    }

    @GetMapping("/{id}")
    public UserDto.Response getUserInfo(@PathVariable Long id) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("유저를 찾을 수 없습니다."));
        return new UserDto.Response(user);
    }

    @GetMapping("/nickname/{nickname}")
    public UserDto.Response getUserInfo(@PathVariable String nickname) {
        User user = userService
            .getUserInfoByNickname(URLDecoder.decode(nickname, StandardCharsets.UTF_8))
            .orElseThrow(() -> new NotFoundResourceException("유저를 찾을 수 없습니다."));
        return new UserDto.Response(user);
    }

    @PatchMapping
    public UserDto.Response changeUserInfo(@RequestHeader("Access-Token") String token,
        @RequestBody UserDto.Request request) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (userService.isExist(request.getNickname())) {
            throw new AlreadyExistResourceException("중복된 닉네임입니다.");
        }

        if (!userService.changeUserInfo(user,
            request.toEntity(passwordEncoder, departmentService))) {
            throw new RuntimeException("회원 정보 수정에 실패했습니다.");
        }

        return new UserDto.Response(user);
    }

    @PatchMapping("/password")
    public UserDto.Response changePassword(@RequestHeader("Access-Token") String token,
        @RequestBody UserDto.Request request) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!userService.changePassword(user, passwordEncoder.encode(request.getPassword()))) {
            throw new RuntimeException("비밀번호 변경에 실패했습니다.");
        }

        return new UserDto.Response(user);
    }

    @PatchMapping("/teams")
    public ResponseEntity<?> leaveTeam(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!userService.leaveTeam(user)) {
            throw new RuntimeException("팀 탈퇴에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!userService.withdrawal(user)) {
            throw new RuntimeException("회원 탈퇴에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
