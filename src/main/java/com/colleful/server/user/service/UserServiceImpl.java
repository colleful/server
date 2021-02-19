package com.colleful.server.user.service;

import com.colleful.server.user.repository.UserRepository;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserServiceForController, UserServiceForOtherService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseGet(User::getEmptyInstance);
    }

    @Override
    public User getUserIfExist(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
    }

    @Override
    public List<User> getUserByNickname(String nickname) {
        return userRepository.findByNicknameContaining(nickname);
    }

    @Override
    public List<User> getMembers(Long teamId) {
        return userRepository.findAllByTeamId(teamId);
    }

    @Override
    public void changeUserInfo(Long userId, UserDto.Request info) {
        User user = getUserIfExist(userId);

        if (userRepository.existsByNickname(info.getNickname())) {
            throw new ForbiddenBehaviorException("중복된 닉네임입니다.");
        }

        user.changeInfo(info);
    }

    @Override
    public void changePassword(Long userId, String encodedPassword) {
        User user = getUserIfExist(userId);
        user.changePassword(encodedPassword);
    }

    @Override
    public void withdrawal(Long userId) {
        User user = getUserIfExist(userId);

        if (user.hasTeam()) {
            throw new ForbiddenBehaviorException("팀을 먼저 탈퇴해 주세요.");
        }

        userRepository.deleteById(userId);
    }
}
