package com.colleful.server.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.department.repository.DepartmentRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.repository.TeamRepository;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.repository.UserRepository;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class TeamServiceTest {

    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    private User user1;
    private User user2;

    @Autowired
    public TeamServiceTest(TeamService teamService,
        TeamRepository teamRepository,
        UserRepository userRepository,
        DepartmentRepository departmentRepository,
        PasswordEncoder passwordEncoder) {
        this.teamService = teamService;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
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
            .gender(Gender.FEMALE)
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
    public void 팀_생성() {
        TeamDto.Request dto = TeamDto.Request.builder().teamName("컴공 강동원").build();

        teamService.createTeam(dto, user1.getId());

        User user = userRepository.findById(user1.getId()).orElse(null);
        assertThat(user.getTeamId()).isNotNull();
    }

    @Test
    public void 팀_상태_변경_및_정보_조회() {
        TeamDto.Request dto = TeamDto.Request.builder().teamName("컴공 강동원").build();
        teamService.createTeam(dto, user1.getId());

        User user = userRepository.findById(user1.getId()).orElse(null);
        teamService.updateTeamStatus(user.getTeamId(), user1.getId(), TeamStatus.READY);

        Team result = teamService.getTeamInfo(user.getTeamId()).orElse(null);
        assertThat(result.getTeamName()).isEqualTo("컴공 강동원");
    }

    @Test
    public void 팀_이름_변경() {
        TeamDto.Request dto = TeamDto.Request.builder().teamName("컴공 강동원").build();
        teamService.createTeam(dto, user1.getId());

        User user = userRepository.findById(user1.getId()).orElse(null);
        teamService.changeTeamInfo(user.getTeamId(), "컴공 원빈");

        Team result = teamService.getTeamInfo(user.getTeamId()).orElse(null);
        assertThat(result.getTeamName()).isEqualTo("컴공 원빈");
    }

    @Test
    public void 팀_매치_및_삭제() {
        Long team1 = teamService
            .createTeam(TeamDto.Request.builder().teamName("test1").build(), user1.getId());
        Long team2 = teamService
            .createTeam(TeamDto.Request.builder().teamName("test2").build(), user2.getId());

        teamService.saveMatchInfo(team1, team2);

        Team result1 = teamRepository.findById(team1).orElse(null);
        Team result2 = teamRepository.findById(team2).orElse(null);
        assertThat(result1.getMatchedTeamId()).isEqualTo(result2.getId());
        assertThat(result2.getMatchedTeamId()).isEqualTo(result1.getId());

        teamService.deleteTeam(team2);

        Team result3 = teamRepository.findById(team1).orElse(null);
        Team result4 = teamRepository.findById(team2).orElse(null);
        assertThat(result3.getMatchedTeamId()).isNull();
        assertThat(result4).isNull();
    }
}
