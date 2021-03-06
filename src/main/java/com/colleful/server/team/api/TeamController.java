package com.colleful.server.team.api;

import com.colleful.server.global.security.JwtProperties;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.dto.TeamDto;
import com.colleful.server.team.service.TeamServiceForController;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.dto.UserDto;
import com.colleful.server.global.dto.PageDto;
import com.colleful.server.global.security.JwtProvider;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    private final TeamServiceForController teamService;
    private final JwtProvider provider;

    @GetMapping
    public PageDto.Response<TeamDto.Response> getAllReadyTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllReadyTeams(request);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeam(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        Team team = teamService.getTeam(provider.getId(token), id);
        return team.isNotEmpty()
            ? ResponseEntity.ok(new TeamDto.Response(team))
            : ResponseEntity.noContent().build();
    }

    @GetMapping("/team-name/{team-name}")
    public PageDto.Response<TeamDto.Response> searchTeams(@PageableDefault Pageable request,
        @PathVariable("team-name") String teamName) {
        Page<Team> teams = teamService.searchTeams(request, teamName);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @GetMapping("/{id}/members")
    public List<UserDto.Response> getMembers(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        List<User> users = teamService.getMembers(provider.getId(token), id);
        return users.stream().map(UserDto.Response::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestHeader(JwtProperties.HEADER) String token,
        @RequestBody TeamDto.Request request) {
        Team team = teamService.createTeam(provider.getId(token), request.getTeamName());
        return ResponseEntity.created(URI.create("/api/teams/" + team.getId()))
            .body(new TeamDto.Response(team));
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveTeam(@RequestHeader(JwtProperties.HEADER) String token) {
        teamService.leaveTeam(provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finish-match")
    public ResponseEntity<?> finishMatch(@RequestHeader(JwtProperties.HEADER) String token) {
        teamService.finishMatch(provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ready")
    public ResponseEntity<?> ready(@RequestHeader(JwtProperties.HEADER) String token) {
        teamService.changeStatus(provider.getId(token), TeamStatus.READY);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/watching")
    public ResponseEntity<?> watching(@RequestHeader(JwtProperties.HEADER) String token) {
        teamService.changeStatus(provider.getId(token), TeamStatus.WATCHING);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTeam(@RequestHeader(JwtProperties.HEADER) String token) {
        teamService.deleteTeam(provider.getId(token));
        return ResponseEntity.ok().build();
    }
}
