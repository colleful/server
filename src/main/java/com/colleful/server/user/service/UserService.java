package com.colleful.server.user.service;

import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import java.util.List;

public interface UserService {

    User getUser(Long userId);

    List<User> getUserByNickname(String nickname);

    List<User> getMembers(Long teamId);

    void changeUserInfo(Long userId, UserDto.Request info);

    void changePassword(Long userId, String encodedPassword);

    void withdrawal(Long userId);
}