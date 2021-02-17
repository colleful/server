package com.colleful.server.user.service;

import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceForOtherServiceImpl implements UserServiceForOtherService {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
    }

    @Override
    public List<User> getMembers(Long teamId) {
        return userRepository.findAllByTeamId(teamId);
    }
}
