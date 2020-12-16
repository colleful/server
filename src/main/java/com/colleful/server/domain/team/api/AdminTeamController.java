package com.colleful.server.domain.team.api;

import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.dto.TeamDto.Response;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.global.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/team")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminTeamController {

    private final TeamService teamService;

    @GetMapping
    public PageDto.Response<Response> getAllTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllTeams(request);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeamInfo(@PathVariable Long id
        , @RequestBody TeamDto.Request request) {
        teamService.changeTeamInfo(id, request.getTeamName());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
