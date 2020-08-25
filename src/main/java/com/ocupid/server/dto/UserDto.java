package com.ocupid.server.dto;

import com.ocupid.server.domain.User;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDto {

    @Getter
    public static class Request {

        private String email;
        private String password;
        private String gender;
        private String college;

        public User toEntity(PasswordEncoder passwordEncoder) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setGender(gender);
            user.setCollege(college);
            user.setRoles(Collections.singletonList("ROLE_USER"));
            return user;
        }
    }

    @Getter
    public static class Response {

        private final Long id;
        private final String email;
        private final String gender;
        private final String college;

        public Response(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.gender = user.getGender();
            this.college = user.getCollege();
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
}
