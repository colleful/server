package com.colleful.server.user.service;

import com.colleful.server.department.service.DepartmentService;
import com.colleful.server.global.exception.ErrorType;
import com.colleful.server.user.domain.Gender;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.repository.UserRepository;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.NotMatchedPasswordException;
import com.colleful.server.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserServiceForOtherService userService;
    private final EmailServiceForOtherService emailService;
    private final DepartmentService departmentService;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User join(UserDto.Request dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistResourceException(ErrorType.ALREADY_EXIST_EMAIL);
        }

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new AlreadyExistResourceException(ErrorType.ALREADY_EXIST_NICKNAME);
        }

        emailService.checkVerification(dto.getEmail());

        User user = User.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .nickname(dto.getNickname())
            .birthYear(dto.getBirthYear())
            .gender(Gender.valueOf(dto.getGender()))
            .department(departmentService.getDepartment(dto.getDepartmentId()))
            .selfIntroduction(dto.getSelfIntroduction())
            .role("ROLE_USER")
            .build();
        userRepository.save(user);

        return user;
    }

    @Override
    public String login(UserDto.LoginRequest dto) {
        User user = userService.getUserIfExist(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new NotMatchedPasswordException();
        }

        return provider.createToken(user.getEmail(), user.getId(), user.getRole());
    }

    @Override
    public void changePassword(UserDto.LoginRequest dto) {
        User user = userService.getUserIfExist(dto.getEmail());

        emailService.checkVerification(dto.getEmail());

        user.changePassword(passwordEncoder.encode(dto.getPassword()));
    }
}
