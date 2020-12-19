package com.colleful.server.domain.invitation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.department.repository.DepartmentRepository;
import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.repository.TeamRepository;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class InvitationServiceTest {

    private final InvitationService invitationService;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    private User user1;
    private User user2;
    private Team team1;

    @Autowired
    public InvitationServiceTest(
        InvitationService invitationService,
        TeamService teamService,
        UserRepository userRepository,
        TeamRepository teamRepository,
        DepartmentRepository departmentRepository,
        PasswordEncoder passwordEncoder) {
        this.invitationService = invitationService;
        this.teamService = teamService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    public void 사용자_및_팀_생성() {
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

        Long teamId = teamService.createTeam(TeamDto.Request
            .builder().teamName("test team1").build(), this.user1.getId());
        this.team1 = teamRepository.findById(teamId).orElse(null);
        this.user1 = userRepository.findById(this.user1.getId()).orElse(null);
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
    public void 초대() {
        invitationService.invite(team1.getId(), user2.getId(), user1.getId());

        List<Invitation> results = invitationService.getAllInvitations(user2.getId());
        assertThat(results.size()).isEqualTo(1);
        results.forEach(result -> invitationService.deleteInvitationInfo(result.getId()));
    }

    @Test
    public void 초대_수락() {
        invitationService.invite(team1.getId(), user2.getId(), user1.getId());
        List<Invitation> results = invitationService.getAllInvitations(user2.getId());

        invitationService.accept(results.get(0).getId(), user2.getId());

        User result = userRepository.findById(user2.getId()).orElse(null);
        assertThat(result.getTeamId()).isEqualTo(team1.getId());
    }

    @Test
    public void 초대_거절() {
        invitationService.invite(team1.getId(), user2.getId(), user1.getId());
        List<Invitation> results = invitationService.getAllInvitations(user2.getId());

        invitationService.refuse(results.get(0).getId(), user2.getId());

        User result = userRepository.findById(user2.getId()).orElse(null);
        assertThat(result.getTeamId()).isNull();
    }
}
