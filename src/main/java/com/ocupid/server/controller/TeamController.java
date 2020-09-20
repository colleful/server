package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamInvitation;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.TeamStatus;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.TeamDto.*;
import com.ocupid.server.exception.ForbiddenBehaviorException;
import com.ocupid.server.exception.NotFoundResourceException;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.TeamInvitationService;
import com.ocupid.server.service.TeamMemberService;
import com.ocupid.server.service.TeamService;
import com.ocupid.server.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;
    private final TeamInvitationService teamInvitationService;
    private final UserService userService;
    private final JwtProvider provider;

    public TeamController(TeamService teamService,
        TeamInvitationService teamInvitationService,
        UserService userService,
        JwtProvider provider) {
        this.teamService = teamService;
        this.teamInvitationService = teamInvitationService;
        this.userService = userService;
        this.provider = provider;
    }

    @PostMapping
    public Response createTeam(@RequestHeader(value = "Access-Token") String token,
        @RequestBody Request request) {
        User leader = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("리더 정보를 찾을 수 없습니다."));
        Team team = request.toEntity(leader);

        if (!teamService.createTeam(team)) {
            throw new RuntimeException();
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setMember(team.getLeader());
        team.getMembers().add(member);
        return new Response(team);
    }

    @PostMapping("/invitations/{team-id}/{user-id}")
    public ResponseEntity<?> createMember(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("team-id") Long teamId, @PathVariable("user-id") Long userId) {
        Team team = teamService.getTeamInfo(teamId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (teamInvitationService.alreadyInvited(team, user)) {
            throw new ForbiddenBehaviorException("이미 초대했습니다.");
        }

        if (team.getLeader().getGender().compareTo(user.getGender()) != 0) {
            throw new ForbiddenBehaviorException("같은 성별만 초대할 수 있습니다.");
        }

        TeamInvitation invitation = new TeamInvitation();
        invitation.setTeam(team);
        invitation.setUser(user);

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 초대할 수 있습니다.");
        }

        if (!teamInvitationService.invite(invitation)) {
            throw new RuntimeException("초대에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping
    public List<Response> getAllReadyTeams() {
        List<Response> results = new ArrayList<>();
        List<Team> teams = teamService.getAllReadyTeams();
        for (Team team : teams) {
            results.add(new Response(team));
        }
        return results;
    }

    @GetMapping("/{id}")
    public Response getTeamInfo(@PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.getStatus().compareTo(TeamStatus.READY) != 0) {
            throw new ForbiddenBehaviorException("준비 상태에 있는 팀만 정보를 볼 수 있습니다.");
        }

        return new Response(team);
    }

    @PatchMapping("/{id}")
    public Response updateTeamStatus(@RequestHeader(value = "Access-Token") String token,
        @PathVariable Long id, @RequestBody Request request) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 팀 상태를 변경할 수 있습니다.");
        }

        if (!teamService.updateTeamStatus(team, TeamStatus.valueOf(request.getStatus()))) {
            throw new RuntimeException("상태 변경에 실패했습니다.");
        }

        return new Response(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 팀을 삭제할 수 있습니다.");
        }

        if (!teamService.deleteTeam(id)) {
            throw new RuntimeException("팀 삭제에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
