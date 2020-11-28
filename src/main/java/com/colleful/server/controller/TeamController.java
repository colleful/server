package com.colleful.server.controller;

import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamMember;
import com.colleful.server.domain.TeamStatus;
import com.colleful.server.domain.User;
import com.colleful.server.dto.PageDto;
import com.colleful.server.dto.TeamDto;
import com.colleful.server.dto.TeamDto.*;
import com.colleful.server.exception.ForbiddenBehaviorException;
import com.colleful.server.exception.NotFoundResourceException;
import com.colleful.server.security.JwtProvider;
import com.colleful.server.service.TeamMemberService;
import com.colleful.server.service.TeamService;
import com.colleful.server.service.UserService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final TeamMemberService teamMemberService;
    private final UserService userService;
    private final JwtProvider provider;

    public TeamController(TeamService teamService,
        TeamMemberService teamMemberService,
        UserService userService,
        JwtProvider provider) {
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
        this.userService = userService;
        this.provider = provider;
    }

    @PostMapping
    public Response createTeam(@RequestHeader(value = "Access-Token") String token,
        @RequestBody Request request) {
        User leader = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("리더 정보를 찾을 수 없습니다."));
        Team team = request.toEntity(leader);

        if (!teamService.createTeam(team, leader)) {
            throw new RuntimeException();
        }

        List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
        return new Response(team, members);
    }

    @GetMapping
    public PageDto.Response<Response> getAllReadyTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllReadyTeams(request);
        Page<Response> responses = teams.map(team -> {
            List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
            return new Response(team, members);
        });
        return new PageDto.Response<>(responses);
    }

    @GetMapping("/{id}")
    public Response getTeamInfo(@PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotReady()) {
            throw new ForbiddenBehaviorException("준비 상태에 있는 팀만 정보를 볼 수 있습니다.");
        }

        List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
        return new Response(team, members);
    }

    @GetMapping("/team-name/{team-name}")
    public PageDto.Response<Response> searchTeams(@PageableDefault Pageable request,
        @PathVariable("team-name") String teamName) {
        Page<Team> teams = teamService.searchTeams(request, teamName);
        Page<Response> responses = teams.map(team -> {
            List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
            return new Response(team, members);
        });
        return new PageDto.Response<>(responses);
    }

    @PatchMapping("/{id}")
    public Response updateTeamStatus(@RequestHeader(value = "Access-Token") String token,
        @PathVariable Long id, @RequestBody Request request) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotLeader(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 팀 상태를 변경할 수 있습니다.");
        }

        if (!teamService.updateTeamStatus(team, TeamStatus.valueOf(request.getStatus()))) {
            throw new RuntimeException("상태 변경에 실패했습니다.");
        }

        List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
        return new Response(team, members);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotLeader(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 팀을 삭제할 수 있습니다.");
        }

        if (!teamService.deleteTeam(team)) {
            throw new RuntimeException("팀 삭제에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/members")
    public ResponseEntity<?> leaveTeam(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀 정보가 없습니다."));
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!teamMemberService.leaveTeam(team, user)) {
            throw new RuntimeException("팀 탈퇴에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}