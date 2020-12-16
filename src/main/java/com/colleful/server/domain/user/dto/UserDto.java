package com.colleful.server.domain.user.dto;

import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.domain.department.service.DepartmentService;
import java.util.Calendar;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDto {

    @Getter
    public static class Request {

        private String email;
        private String password;
        private String nickname;
        private Integer birthYear;
        private String gender;
        private Long departmentId;
        private String selfIntroduction;

        public User toEntity(PasswordEncoder passwordEncoder,
            DepartmentService departmentService) {
            Department department = departmentService.getDepartment(departmentId)
                .orElseThrow(() -> new NotFoundResourceException("학과 정보가 없습니다."));
            return User.builder()
                .email(email)
                .password(password == null ? null : passwordEncoder.encode(password))
                .nickname(nickname)
                .birthYear(birthYear)
                .gender(gender == null ? null : Gender.valueOf(gender))
                .department(department)
                .selfIntroduction(selfIntroduction)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        }
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

        public Response(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.age = Calendar.getInstance().get(Calendar.YEAR) - user.getBirthYear() + 1;
            this.gender = user.getGender().name();
            this.department = user.getDepartment().getDepartmentName();
            this.selfIntroduction = user.getSelfIntroduction();
        }
    }

    @Getter
    public static class LoginRequest {

        private String email;
        private String password;
    }

    @Getter
    public static class LoginResponse {

        private final String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }

    @Getter
    public static class EmailRequest {

        private String email;
        private Integer code;
    }
}
