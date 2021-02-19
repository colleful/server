package com.colleful.server.user.service;

import com.colleful.server.user.domain.User;
import java.util.List;

public interface UserServiceForOtherService {

    User getUserIfExist(Long userId);

    List<User> getMembers(Long teamId);
}
