package com.colleful.server.domain.user;

import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.domain.user.Gender;
import com.colleful.server.domain.teaminvitation.TeamInvitation;
import com.colleful.server.domain.user.User;
import com.colleful.server.domain.department.DepartmentService;
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

        public User toEntity(PasswordEncoder passwordEncoder, DepartmentService departmentService) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password == null ? null : passwordEncoder.encode(password));
            user.setNickname(nickname);
            user.setBirthYear(birthYear);
            user.setGender(gender == null ? null : Gender.valueOf(gender));
            user.setDepartment(departmentId == null ? null
                : departmentService
                    .getDepartment(departmentId)
                    .orElseThrow(() -> new NotFoundResourceException("학과 정보가 없습니다.")));
            user.setSelfIntroduction(selfIntroduction);
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

    @Getter
    public static class InvitationResponse {

        private final Long id;
        private final Long teamId;
        private final String teamName;

        public InvitationResponse(TeamInvitation invitation) {
            this.id = invitation.getId();
            this.teamId = invitation.getTeam().getId();
            this.teamName = invitation.getTeam().getTeamName();
        }
    }
}
