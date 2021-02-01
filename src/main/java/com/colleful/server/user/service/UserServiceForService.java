package com.colleful.server.user.service;

import com.colleful.server.user.domain.User;
import java.util.List;

public interface UserServiceForService {

    User getUser(Long userId);

    List<User> getMembers(Long teamId);
}
