package com.colleful.server.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.domain.user.repository.UserRepository;
import com.colleful.server.global.exception.AlreadyExistResourceException;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 회원_이름_자기소개_변경() {
        UserDto.Request dto1 = UserDto.Request.builder()
            .nickname("박성팔")
            .selfIntroduction("안녕하세요.")
            .build();

        when(userRepository.findById(1L))
            .thenReturn(User.builder().nickname("박성필").selfIntroduction("안녕").build());

        userService.changeUserInfo(1L, dto1);

        User result1 = userRepository.findById(1L).orElse(User.builder().build());
        assertThat(result1.getNickname()).isEqualTo("박성팔");
        assertThat(result1.getSelfIntroduction()).isEqualTo("안녕하세요.");
    }

    @Test
    public void 회원_닉네임_변경() {
        UserDto.Request dto = UserDto.Request.builder()
            .nickname("박성팔")
            .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder()
            .nickname("박성필").selfIntroduction("안녕").build()));

        userService.changeUserInfo(2L, dto);

        User result = userRepository.findById(2L).orElse(User.builder().build());
        assertThat(result.getNickname()).isEqualTo("박성팔");
        assertThat(result.getSelfIntroduction()).isEqualTo("안녕");
    }

    @Test
    public void 회원_닉네임_중복() {
        UserDto.Request dto = UserDto.Request.builder()
            .nickname("박성팔")
            .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder()
            .nickname("박성필").selfIntroduction("안녕").build()));
        when(userRepository.existsByNickname("박성팔")).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserInfo(2L, dto))
            .isInstanceOf(AlreadyExistResourceException.class);
    }

    @Test
    public void 회원_자기소개_변경() {
        UserDto.Request dto = UserDto.Request.builder()
            .selfIntroduction("안녕하세요.")
            .build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(User.builder()
            .nickname("박성필").selfIntroduction("안녕").build()));

        userService.changeUserInfo(3L, dto);

        User result = userRepository.findById(3L).orElse(User.builder().build());
        assertThat(result.getNickname()).isEqualTo("박성필");
        assertThat(result.getSelfIntroduction()).isEqualTo("안녕하세요.");
    }

    @Test
    public void 비밀번호_변경() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(User.builder().password("password").build()));

        userService.changePassword(1L, "new_password");

        User result = userRepository.findById(1L).orElse(User.builder().build());
        assertThat(result.getPassword()).isEqualTo("new_password");
    }

    @Test
    public void 회원탈퇴() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(User.builder().build()));

        userService.withdrawal(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void 회원탈퇴_불가() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(User.builder().teamId(1L).build()));

        assertThatThrownBy(() -> userService.withdrawal(1L))
            .isInstanceOf(ForbiddenBehaviorException.class);
    }
}
