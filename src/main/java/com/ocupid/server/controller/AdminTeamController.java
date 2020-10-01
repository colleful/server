package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.dto.PagingDto;
import com.ocupid.server.dto.TeamDto.*;
import com.ocupid.server.exception.NotFoundResourceException;
import com.ocupid.server.service.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public PagingDto.Response<Response> getAllTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllTeams(request);
        return new PagingDto.Response<>(teams.map(Response::new));
    }

    @PatchMapping("/{id}")
    public Response updateTeamInfo(@PathVariable Long id, @RequestBody Request request) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!teamService.changeTeamInfo(team, request.getTeamName())) {
            throw new RuntimeException("팀 정보 변경에 실패했습니다.");
        }

        return new Response(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        if (!teamService.deleteTeam(id)) {
            throw new RuntimeException("팀 삭제에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
