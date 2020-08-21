package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.dto.TeamDto.*;
import com.ocupid.server.service.TeamService;
import com.ocupid.server.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
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
}
