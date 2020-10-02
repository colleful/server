package com.ocupid.server.service;

import com.ocupid.server.domain.User;
import com.ocupid.server.repository.UserRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Optional<User> getUserInfoByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserInfoByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public Page<User> getAllUserInfo(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public Boolean isExist(String nickname) {
        return userRepository.existsByNickname(nickname);
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
