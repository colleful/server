package com.colleful.server.user.service;

import com.colleful.server.user.dto.UserDto;

public interface AuthService {

    Long join(UserDto.Request dto);

    String login(UserDto.LoginRequest dto);

    void sendEmailForRegistration(String email);

    void sendEmailForPassword(String email);

    void changePassword(UserDto.LoginRequest dto);

    void checkEmail(UserDto.EmailRequest dto);
}
