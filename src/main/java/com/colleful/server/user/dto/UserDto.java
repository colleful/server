package com.colleful.server.user.dto;

import com.colleful.server.user.domain.User;
import java.util.Calendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    @Builder
    public static class Request {

        private final String email;
        private final String password;
        private final String nickname;
        private final Integer birthYear;
        private final String gender;
        private final Long departmentId;
        private final String selfIntroduction;
    }

    @Getter
    public static class Response {

        private final Long id;
        private final String email;
        private final String nickname;
        private final Integer age;
        private final String gender;
        private final String department;
        private final String selfIntroduction;
        private final Long teamId;

        public Response(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.age = Calendar.getInstance().get(Calendar.YEAR) - user.getBirthYear() + 1;
            this.gender = user.getGender().name();
            this.department = user.getDepartment().getDepartmentName();
            this.selfIntroduction = user.getSelfIntroduction();
            this.teamId = user.getTeamId();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class LoginRequest {

        private final String email;
        private final String password;
    }

    @Getter
    @AllArgsConstructor
    public static class EmailRequest {

        private final String email;
        private final Integer code;
    }
}
