package com.ocupid.server.dto;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.TeamDto.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
        private String college;

        public User toEntity(String encodedPassword) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(encodedPassword);
            user.setNickname(nickname);
            user.setBirthYear(birthYear);
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
        private final String nickname;
        private final Integer age;
        private final String gender;
        private final String college;
        private final List<TeamDto.Response> teams;

        public Response(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.age = Calendar.getInstance().get(Calendar.YEAR) - user.getBirthYear() + 1;
            this.gender = user.getGender();
            this.college = user.getCollege();
            this.teams = new ArrayList<>();
            for (TeamMember team : user.getTeams()) {
                this.teams.add(new TeamDto.Response(team.getTeam()));
            }
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
