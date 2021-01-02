package com.colleful.server.domain.user.service;

import com.colleful.server.domain.user.repository.UserRepository;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User getUserInfo(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
    }

    public List<User> getUserInfoByNickname(String nickname) {
        return userRepository.findByNicknameContaining(nickname);
    }

    public List<User> getMembers(Long teamId) {
        return userRepository.findAllByTeamId(teamId);
    }

    public void changeUserInfo(Long userId, UserDto.Request info) {
        User user = getUserInfo(userId);

        if (userRepository.existsByNickname(info.getNickname())
            && !user.getNickname().equals(info.getNickname())) {
            throw new AlreadyExistResourceException("중복된 닉네임입니다.");
        }

        user.changeInfo(info);
    }

    public void changePassword(Long userId, String encodedPassword) {
        User user = getUserInfo(userId);
        user.changePassword(encodedPassword);
    }

    public void withdrawal(Long userId) {
        User user = getUserInfo(userId);

        if (!user.isNotOnAnyTeam()) {
            throw new ForbiddenBehaviorException("팀을 먼저 탈퇴해 주세요.");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(RuntimeException::new);
    }
}
