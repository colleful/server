package com.colleful.server.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.department.repository.DepartmentRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.repository.TeamRepository;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.domain.user.repository.UserRepository;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserServiceTest {

    private final UserService userService;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    private User user1;
    private User user2;

    @Autowired
    public UserServiceTest(UserService userService,
        TeamService teamService,
        UserRepository userRepository,
        TeamRepository teamRepository,
        DepartmentRepository departmentRepository,
        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.teamService = teamService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    public void 사용자_생성() {
        Department department = departmentRepository.findById(24L).orElse(null);
        User user1 = User.builder()
            .email("ssphil21e@jbnu.ac.kr")
            .password(passwordEncoder.encode("aaa123"))
            .nickname("박성필")
            .birthYear(1999)
            .gender(Gender.MALE)
            .department(department)
            .selfIntroduction("ㅎㅇ")
            .roles(Collections.singletonList("ROLE_USER"))
            .build();
        userRepository.save(user1);
        this.user1 = userRepository.findByEmail("ssphil21e@jbnu.ac.kr").orElse(null);

        User user2 = User.builder()
            .email("voiciphil@jbnu.ac.kr")
            .password(passwordEncoder.encode("abc123"))
            .nickname("패트")
            .birthYear(1999)
            .gender(Gender.MALE)
            .department(department)
            .selfIntroduction("안녕")
            .roles(Collections.singletonList("ROLE_USER"))
            .build();
        userRepository.save(user2);
        this.user2 = userRepository.findByEmail("voiciphil@jbnu.ac.kr").orElse(null);
    }

    @AfterEach
    public void 사용자_삭제() {
        if (user1 != null) {
            user1 = userRepository.findById(user1.getId()).orElse(null);
            userRepository.deleteById(user1.getId());
        }

        if (user2 != null) {
            user2 = userRepository.findById(user2.getId()).orElse(null);
            userRepository.deleteById(user2.getId());
        }

        if (user1 != null && user1.getTeamId() != null) {
            teamRepository.deleteById(user1.getTeamId());
        }

        if (user2 != null && user2.getTeamId() != null
            && !user1.getTeamId().equals(user2.getTeamId())) {
            teamRepository.deleteById(user2.getTeamId());
        }
    }

    @Test
    public void 회원_정보_변경() {
        UserDto.Request dto = UserDto.Request.builder()
            .nickname("박성팔")
            .selfIntroduction("안녕하세요.")
            .build();

        userService.changeUserInfo(user1.getId(), dto);

        User result = userRepository.findById(user1.getId()).orElse(null);
        assertThat(result.getNickname()).isEqualTo("박성팔");
        assertThat(result.getSelfIntroduction()).isEqualTo("안녕하세요.");
    }

    @Test
    public void 비밀번호_변경() {
        userService.changePassword(user1.getId(), passwordEncoder.encode("abc1234"));

        User result = userRepository.findById(user1.getId()).orElse(null);
        assertThat(passwordEncoder.matches("abc1234", result.getPassword())).isTrue();
    }

    @Test
    public void 팀_가입() {
        TeamDto.Request dto = TeamDto.Request.builder().teamName("sample").build();
        Long teamId = teamService.createTeam(dto, user1.getId());

        userService.joinTeam(user2.getId(), teamId);

        User result = userRepository.findById(user2.getId()).orElse(null);
        assertThat(result.getTeamId()).isNotNull();
    }

    @Test
    public void 팀_탈퇴() {
        TeamDto.Request dto = TeamDto.Request.builder().teamName("sample").build();
        Team team = dto.toEntity(user1);
        teamRepository.save(team);
        userService.joinTeam(user2.getId(), team.getId());

        userService.leaveTeam(user2.getId());

        User result = userRepository.findById(user2.getId()).orElse(null);
        assertThat(result.getTeamId()).isNull();
    }

    @Test
    public void 회원탈퇴() {
        userService.withdrawal(user1.getId());

        User result = userRepository.findById(user1.getId()).orElse(null);
        assertThat(result).isNull();
        user1 = null;
    }
}
