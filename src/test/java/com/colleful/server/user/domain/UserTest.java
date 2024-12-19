package com.colleful.server.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

    User user;

    @BeforeEach
    void init() {
        user = User.builder()
            .nickname("nickname")
            .selfIntroduction("selfIntroduction")
            .build();
    }

    @Test
    void 빈_객체() {
        user = User.getEmptyInstance();

        boolean isNotEmpty = user.isNotEmpty();

        assertThat(isNotEmpty).isFalse();
    }

    @Test
    void 빈_객체_아님() {
        boolean isNotEmpty = user.isNotEmpty();

        assertThat(isNotEmpty).isTrue();
    }

    @Test
    void 닉네임_변경() {
        UserDto.Request info = UserDto.Request.builder().nickname("new_nickname").build();

        user.changeInfo(info);

        assertThat(user.getNickname()).isEqualTo("new_nickname");
        assertThat(user.getSelfIntroduction()).isEqualTo("selfIntroduction");
    }

    @Test
    void 자기소개_변경() {
        UserDto.Request info = UserDto.Request.builder()
            .selfIntroduction("new_selfIntroduction")
            .build();

        user.changeInfo(info);

        assertThat(user.getNickname()).isEqualTo("nickname");
        assertThat(user.getSelfIntroduction()).isEqualTo("new_selfIntroduction");
    }

    @Test
    void 닉네임_자기소개_변경() {
        UserDto.Request info = UserDto.Request.builder()
            .nickname("new_nickname")
            .selfIntroduction("new_selfIntroduction")
            .build();

        user.changeInfo(info);

        assertThat(user.getNickname()).isEqualTo("new_nickname");
        assertThat(user.getSelfIntroduction()).isEqualTo("new_selfIntroduction");
    }

    @Test
    void 비밀번호_변경() {
        user.changePassword("new_password");

        assertThat(user.getPassword()).isEqualTo("new_password");
    }

    @Test
    void 팀_가입() {
        user.joinTeam(1L);

        assertThat(user.hasTeam()).isTrue();
        assertThat(user.hasNotTeam()).isFalse();
        assertThat(user.isNotMemberOf(1L)).isFalse();
    }

    @Test
    void 이미_팀에_속해있는_경우_다른_팀_가입_불가() {
        user.joinTeam(1L);

        Throwable thrown = catchThrowable(() -> user.joinTeam(2L));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    void 팀_탈퇴() {
        user.joinTeam(1L);

        user.leaveTeam();

        assertThat(user.hasTeam()).isFalse();
        assertThat(user.hasNotTeam()).isTrue();
        assertThat(user.isNotMemberOf(1L)).isTrue();
    }
}
