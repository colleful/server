package com.colleful.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChangingInfoTest {

    @InjectMocks
    UserServiceImpl userServiceImpl;
    @Mock
    UserRepository userRepository;

    User user;

    @BeforeEach
    void init() {
        user = User.builder().id(1L).nickname("박성필").build();
    }

    @Test
    void 회원_닉네임_중복_불가() {
        UserDto.Request dto = UserDto.Request.builder().nickname("박성팔").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname(any())).willReturn(true);

        Throwable thrown = catchThrowable(() -> userServiceImpl.changeUserInfo(1L, dto));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }
}
