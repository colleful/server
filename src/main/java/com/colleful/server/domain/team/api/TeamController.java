package com.colleful.server.domain.team.api;

import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.dto.TeamDto.Response;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.global.dto.PageDto;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JwtProvider provider;

    @GetMapping
    public PageDto.Response<Response> getAllReadyTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllReadyTeams(request);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @GetMapping("/{id}")
    public TeamDto.Response getTeamInfo(@RequestHeader(value = "Access-Token") String token,
        @PathVariable Long id) {
        Team team = teamService.getTeamInfo(id, provider.getId(token));
        return new TeamDto.Response(team);
    }

    @GetMapping("/team-name/{team-name}")
    public PageDto.Response<TeamDto.Response> searchTeams(@PageableDefault Pageable request,
        @PathVariable("team-name") String teamName) {
        Page<Team> teams = teamService.searchTeams(request, teamName);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestHeader(value = "Access-Token") String token,
        @RequestBody TeamDto.Request request) {
        teamService.createTeam(request, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveTeam(@RequestHeader("Access-Token") String token) {
        teamService.leaveTeam(provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeamStatus(@RequestHeader(value = "Access-Token") String token,
        @PathVariable Long id, @RequestBody TeamDto.Request request) {
        teamService.updateTeamStatus(id, provider.getId(token),
            TeamStatus.valueOf(request.getStatus()));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTeam(@RequestHeader("Access-Token") String token) {
        teamService.deleteTeam(provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
