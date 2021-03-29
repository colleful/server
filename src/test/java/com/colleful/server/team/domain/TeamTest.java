package com.colleful.server.team.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.user.domain.Gender;
import com.colleful.server.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TeamTest {

    Team team1;
    Team team2;
    User user1;
    User user2;

    @BeforeEach
    void init() {
        user1 = User.builder()
            .id(1L)
            .gender(Gender.MALE)
            .build();
        user2 = User.builder()
            .id(2L)
            .gender(Gender.FEMALE)
            .build();
        team1 = Team.of("test1", user1);
        team1.setId(1L);
        team2 = Team.of("test2", user2);
        team2.setId(2L);
    }

    @Test
    void 빈_객체() {
        team1 = Team.getEmptyInstance();

        boolean isNotEmpty = team1.isNotEmpty();

        assertThat(isNotEmpty).isFalse();
    }

    @Test
    void 빈_객체_아님() {
        boolean isNotEmpty = team1.isNotEmpty();

        assertThat(isNotEmpty).isTrue();
    }

    @Test
    void 리더_확인() {
        boolean isLedByUser1 = team1.isLedBy(user1.getId());
        boolean isNotLedByUser2 = team2.isNotLedBy(user2.getId());

        assertThat(isLedByUser1).isTrue();
        assertThat(isNotLedByUser2).isFalse();
    }

    @Test
    void 성별_확인() {
        boolean hasSameGenderWithUser1 = team1.hasSameGenderWith(user1.getGender());
        boolean hasDifferentGenderFromUser2 = team2.hasDifferentGenderFrom(user2.getGender());

        assertThat(hasSameGenderWithUser1).isTrue();
        assertThat(hasDifferentGenderFromUser2).isFalse();
    }

    @Test
    void 상태_확인() {
        boolean isNotReady = team1.isNotReady();
        boolean isNotPending = team1.isNotPending();
        boolean isMatched = team1.isMatched();
        boolean isNotMatched = team1.isNotMatched();

        assertThat(isNotReady).isTrue();
        assertThat(isNotPending).isFalse();
        assertThat(isMatched).isFalse();
        assertThat(isNotMatched).isTrue();
    }

    @Test
    void 매칭() {
        team1.match(team2.getId());
        team2.match(team1.getId());

        assertThat(team1.isMatched()).isTrue();
        assertThat(team2.isMatched()).isTrue();
    }

    @Test
    void 이미_매칭된_팀과_매칭_불가() {
        User user3 = User.builder().id(3L).gender(Gender.FEMALE).build();
        Team team3 = Team.of("test3", user3);
        team3.setId(3L);
        team1.match(team3.getId());
        team3.match(team1.getId());

        Throwable thrown = catchThrowable(() -> team1.match(team2.getId()));

        assertThat(thrown).isInstanceOf(ForbiddenBehaviorException.class);
    }

    @Test
    void 매칭_끝내기() {
        team1.match(team2.getId());
        team2.match(team1.getId());

        team1.finishMatch();
        team2.finishMatch();

        assertThat(team1.isMatched()).isFalse();
        assertThat(team2.isMatched()).isFalse();
        assertThat(team1.isNotPending()).isFalse();
        assertThat(team2.isNotPending()).isFalse();
    }
}
