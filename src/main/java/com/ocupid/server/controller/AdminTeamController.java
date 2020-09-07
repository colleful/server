package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.dto.TeamDto.*;
import com.ocupid.server.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/team")
@CrossOrigin(origins = "*")
public class AdminTeamController {

    private final TeamService teamService;

    public AdminTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<Response> getAllTeams() {
        List<Response> responses = new ArrayList<>();
        List<Team> teams = teamService.getAllTeams();
        for (Team team : teams) {
            responses.add(new Response(team));
        }

        return responses;
    }

    @PatchMapping("/{id}")
    public Response updateTeamInfo(@PathVariable Long id, @RequestBody Request request) {
        Team team = teamService.getTeamInfo(id).orElseThrow(RuntimeException::new);

        if(!teamService.ChangeTeamInfo(team,request.getTeamName())) {
            throw new RuntimeException(request.getTeamName());
        }

        return new Response(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        if (!teamService.deleteTeam(id)) {
            throw new RuntimeException();
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
