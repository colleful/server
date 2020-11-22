package com.colleful.server.controller;

import com.colleful.server.domain.User;
import com.colleful.server.dto.PageDto;
import com.colleful.server.dto.UserDto.*;
import com.colleful.server.exception.NotFoundResourceException;
import com.colleful.server.service.DepartmentService;
import com.colleful.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    private final UserService userService;
    private final DepartmentService departmentService;

    public AdminUserController(UserService userService,
        DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public PageDto.Response<Response> getAllUserInfo(@PageableDefault Pageable pageable) {
        Page<User> users = userService.getAllUserInfo(pageable);
        return new PageDto.Response<>(users.map(Response::new));
    }

    @GetMapping("/{id}")
    public Response getUserInfoById(@PathVariable Long id) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        return new Response(user);
    }

    @PatchMapping("/{id}")
    public Response changeUserInfo(@PathVariable Long id, @RequestBody Request request) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!userService.changeUserInfo(user,
            request.toEntity(null, departmentService))) {
            throw new RuntimeException("회원 정보 수정에 실패했습니다.");
        }

        return new Response(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!userService.withdrawal(user)) {
            throw new RuntimeException("회원 탈퇴에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
