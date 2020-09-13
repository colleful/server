package com.ocupid.server.service;

import com.ocupid.server.domain.User;
import com.ocupid.server.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

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

    public List<User> getAllUserInfo(){
        return userRepository.findAll();
    }

    public Boolean isExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean changeUserInfo(User from, User to) {
        try {
            Optional.of(to).map(User::getNickname).ifPresent(from::setNickname);
            Optional.of(to).map(User::getDepartment).ifPresent(from::setDepartment);
            Optional.of(to).map(User::getSelfIntroduction).ifPresent(from::setSelfIntroduction);
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(RuntimeException::new);
    }
}
