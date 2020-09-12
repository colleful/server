package com.ocupid.server.controller;

import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.*;
import com.ocupid.server.service.DepartmentService;
import com.ocupid.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

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
    public List<Response> getAllUserInfo() {
        List<Response> results = new ArrayList<>();
        List<User> users = userService.getAllUserInfo();
        for (User user : users) {
            results.add(new Response(user));
        }

        return results;
    }

    @GetMapping("/{id}")
    public Response getUserInfoById(@PathVariable Long id) {
        User user = userService.getUserInfo(id).orElseThrow(RuntimeException::new);
        return new Response(user);
    }

    @PatchMapping("/{id}")
    public Response changeUserInfo(@PathVariable Long id, @RequestBody Request request) {
        User user = userService.getUserInfo(id).orElseThrow(RuntimeException::new);

        if (!userService.changeUserInfo(user,
            request.toEntity(null, departmentService))) {
            throw new RuntimeException();
        }

        return new Response(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userService.withdrawal(id)) {
            throw new RuntimeException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
