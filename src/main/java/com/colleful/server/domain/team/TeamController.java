package com.colleful.server.domain.team;

import com.colleful.server.global.dto.PageDto;
import com.colleful.server.domain.team.TeamDto.*;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.security.JwtProvider;
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
    private final JwtProvider provider;

    public TeamController(TeamService teamService, JwtProvider provider) {
        this.teamService = teamService;
        this.provider = provider;
    }

    @GetMapping
    public PageDto.Response<Response> getAllReadyTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllReadyTeams(request);
        return new PageDto.Response<>(teams.map(Response::new));
    }

    @GetMapping("/{id}")
    public Response getTeamInfo(@PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotReady()) {
            throw new ForbiddenBehaviorException("준비 상태에 있는 팀만 정보를 볼 수 있습니다.");
        }

        return new Response(team);
    }

    @GetMapping("/team-name/{team-name}")
    public PageDto.Response<Response> searchTeams(@PageableDefault Pageable request,
        @PathVariable("team-name") String teamName) {
        Page<Team> teams = teamService.searchTeams(request, teamName);
        return new PageDto.Response<>(teams.map(Response::new));
    }

    @PostMapping
    public Response createTeam(@RequestHeader(value = "Access-Token") String token,
        @RequestBody Request request) {
        Team team = request.toEntity(provider.getId(token));

        if (!teamService.createTeam(team)) {
            throw new RuntimeException();
        }

        return new Response(team);
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

        return new Response(team);
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
}
