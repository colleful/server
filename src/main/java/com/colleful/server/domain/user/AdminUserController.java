package com.colleful.server.domain.user;

import com.colleful.server.global.dto.PageDto;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.domain.department.DepartmentService;
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
    public PageDto.Response<UserDto.Response> getAllUserInfo(@PageableDefault Pageable pageable) {
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
    public UserDto.Response changeUserInfo(@PathVariable Long id,
        @RequestBody UserDto.Request request) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!userService.changeUserInfo(user,
            request.toEntity(null, departmentService))) {
            throw new RuntimeException("회원 정보 수정에 실패했습니다.");
        }

        return new UserDto.Response(user);
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
