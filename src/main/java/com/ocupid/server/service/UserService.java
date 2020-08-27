package com.ocupid.server.service;

import com.ocupid.server.domain.User;
import com.ocupid.server.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean join(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<User> getUserInfo(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserInfo(String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean changeUserInfo(User from, User to) {
        try {
            from.setNickname(to.getNickname());
            from.setCollege(to.getCollege());
            userRepository.save(from);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean changePassword(User user, String password) {
        try {
            user.setPassword(password);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean withdrawal(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
