package com.colleful.server.domain.user.api;

import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.domain.user.dto.UserDto.Response;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.dto.PageDto;
import com.colleful.server.global.exception.NotFoundResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public PageDto.Response<Response> getAllUserInfo(@PageableDefault Pageable pageable) {
        Page<User> users = userService.getAllUserInfo(pageable);
        return new PageDto.Response<>(users.map(UserDto.Response::new));
    }

    @GetMapping("/{id}")
    public UserDto.Response getUserInfoById(@PathVariable Long id) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        return new UserDto.Response(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeUserInfo(@PathVariable Long id,
        @RequestBody UserDto.Request request) {
        userService.changeUserInfo(id, request);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.withdrawal(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
