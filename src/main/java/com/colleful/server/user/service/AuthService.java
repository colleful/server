package com.colleful.server.user.service;

import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;

public interface AuthService {

    User join(UserDto.Request dto);

    String login(UserDto.LoginRequest dto);

    void changePassword(UserDto.LoginRequest dto);
}
