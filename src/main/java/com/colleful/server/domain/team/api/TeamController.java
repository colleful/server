package com.colleful.server.domain.team.api;

import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.dto.UserDto;
import com.colleful.server.global.dto.PageDto;
import com.colleful.server.global.security.JwtProvider;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
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
    public PageDto.Response<TeamDto.Response> getAllReadyTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllReadyTeams(request);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @GetMapping("/{id}")
    public TeamDto.Response getTeamInfo(@RequestHeader(value = "Authorization") String token,
        @PathVariable Long id) {
        Team team = teamService.getTeam(id, provider.getId(token));
        return new TeamDto.Response(team);
    }

    @GetMapping("/team-name/{team-name}")
    public PageDto.Response<TeamDto.Response> searchTeams(@PageableDefault Pageable request,
        @PathVariable("team-name") String teamName) {
        Page<Team> teams = teamService.searchTeams(request, teamName);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @GetMapping("/{id}/members")
    public List<UserDto.Response> getMembers(@PathVariable Long id) {
        List<User> users = teamService.getMembers(id);
        return users.stream().map(UserDto.Response::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestHeader(value = "Authorization") String token,
        @RequestBody TeamDto.Request request) {
        Long teamId = teamService.createTeam(request, provider.getId(token));
        return ResponseEntity.created(URI.create("/api/teams/" + teamId)).build();
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveTeam(@RequestHeader("Authorization") String token) {
        teamService.leaveTeam(provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finish-match")
    public ResponseEntity<?> finishMatch(@RequestHeader("Authorization") String token) {
        teamService.finishMatch(provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeamStatus(@RequestHeader(value = "Authorization") String token,
        @PathVariable Long id, @RequestBody TeamDto.Request request) {
        teamService.updateStatus(id, provider.getId(token),
            TeamStatus.valueOf(request.getStatus()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTeam(@RequestHeader("Authorization") String token) {
        teamService.deleteTeam(provider.getId(token));
        return ResponseEntity.ok().build();
    }
}
