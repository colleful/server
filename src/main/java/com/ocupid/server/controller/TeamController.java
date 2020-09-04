package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.TeamDto.*;
import com.ocupid.server.security.JwtProvider;
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
    private final TeamMemberService teamMemberService;
    private final UserService userService;
    private final JwtProvider provider;

    public TeamController(TeamService teamService,
        TeamMemberService teamMemberService, UserService userService,
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
            .orElseThrow(RuntimeException::new);
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

    @PostMapping("/{team-id}/members/{member-id}")
    public Response createMember(@PathVariable("team-id") Long teamId,
        @PathVariable("member-id") Long memberId) {
        TeamMember member = new TeamMember();
        Team team = teamService.getTeamInfo(teamId).orElseThrow(RuntimeException::new);
        User user = userService.getUserInfo(memberId).orElseThrow(RuntimeException::new);
        member.setTeam(team);
        member.setMember(user);

        if (!teamMemberService.addMember(member)) {
            throw new RuntimeException();
        }

        return new Response(team);
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
        Team team = teamService.getTeamInfo(id).orElseThrow(RuntimeException::new);

        if (!team.getStatus().equals("ready")) {
            throw new RuntimeException();
        }

        return new Response(team);
    }

    @PatchMapping("/{id}/{status}")
    public Response updateTeamStatus(@RequestHeader(value = "Access-Token") String token,
        @PathVariable Long id, @PathVariable String status) {
        Team team = teamService.getTeamInfo(id).orElseThrow(RuntimeException::new);

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new RuntimeException();
        }

        if (!teamService.updateTeamStatus(team, status)) {
            throw new RuntimeException();
        }

        return new Response(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        Team team = teamService.getTeamInfo(id).orElseThrow(RuntimeException::new);

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new RuntimeException();
        }

        if (!teamService.deleteTeam(id)) {
            throw new RuntimeException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
