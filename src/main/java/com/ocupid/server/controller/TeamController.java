package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.dto.TeamDto.*;
import com.ocupid.server.service.TeamMemberService;
import com.ocupid.server.service.TeamService;
import com.ocupid.server.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final UserService userService;

    public TeamController(TeamService teamService,
        TeamMemberService teamMemberService, UserService userService) {
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
        this.userService = userService;
    }

    @PostMapping
    public Response createTeam(@RequestBody Request request) {
        Team team = request.toEntity(userService);

        if (!teamService.createTeam(team)) {
            throw new RuntimeException();
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setMember(team.getLeader());
        team.getMembers().add(member);
        return new Response(team);
    }

    @PostMapping("/{team-id}/member/{member-id}")
    public Response createMember(@PathVariable("team-id") Long teamId,
        @PathVariable("member-id") Long memberId) {
        TeamMember member = new TeamMember();
        member.setTeam(teamService.getTeamInfo(teamId).orElseThrow(RuntimeException::new));
        member.setMember(userService.getUserInfo(memberId).orElseThrow(RuntimeException::new));

        if (!teamMemberService.addMember(member)) {
            throw new RuntimeException();
        }

        return new Response(teamService.getTeamInfo(teamId).orElseThrow(RuntimeException::new));
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
        return new Response(teamService.getTeamInfo(id).orElseThrow(RuntimeException::new));
    }

    @PatchMapping("/{id}/{status}")
    public Response updateTeamStatus(@PathVariable Long id, @PathVariable String status) {
        Team team = teamService.getTeamInfo(id).orElseThrow(RuntimeException::new);

        if (status.equals("ready") && team.getHeadcount() > team.getMembers().size()) {
            throw new RuntimeException();
        }

        teamService.updateTeamStatus(team, status);
        return new Response(team);
    }
}
