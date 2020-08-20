package com.ocupid.server.controller;

import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.*;
import com.ocupid.server.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Response create(@RequestBody Request request) {
        User user = request.toEntity();

        if (!userService.join(user)) {
            throw new RuntimeException();
        }

        return new Response(user);
    }
}
