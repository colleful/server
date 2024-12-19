package com.colleful.server.user.service;

import com.colleful.server.user.dto.UserDto;

public interface EmailServiceForController {

    void sendEmailForRegistration(String email);

    void sendEmailForPassword(String email);

    void checkEmail(UserDto.EmailRequest dto);
}
