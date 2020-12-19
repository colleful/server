package com.colleful.server.domain.user.service;

import com.colleful.server.domain.user.repository.UserRepository;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.sun.istack.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public void join(User user) {
        userRepository.save(user);
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

    public List<User> getMembers(Long teamId) {
        return userRepository.findAllByTeamId(teamId);
    }

    public Page<User> getAllUserInfo(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public Boolean isExist(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void changeUserInfo(Long userId, UserDto.Request info) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        user.changeInfo(info);
        userRepository.save(user);
    }

    public void changePassword(Long userId, String encodedPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        user.changePassword(encodedPassword);
        userRepository.save(user);
    }

    public void joinTeam(@NotNull Long userId, @NotNull Long teamId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        user.joinTeam(teamId);
        userRepository.save(user);
    }

    public void leaveTeam(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        user.leaveTeam();
        userRepository.save(user);
    }

    @Transactional
    public void withdrawal(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (user.getTeamId() != null) {
            throw new ForbiddenBehaviorException("팀을 먼저 탈퇴해 주세요.");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(RuntimeException::new);
    }
}
