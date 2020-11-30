package com.colleful.server.domain.team;

import com.colleful.server.global.dto.PageDto;
import com.colleful.server.global.exception.NotFoundResourceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/team")
@CrossOrigin(origins = "*")
public class AdminTeamController {

    private final TeamService teamService;

    public AdminTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public PageDto.Response<TeamDto.Response> getAllTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllTeams(request);
        return new PageDto.Response<>(teams.map(TeamDto.Response::new));
    }

    @PatchMapping("/{id}")
    public TeamDto.Response updateTeamInfo(@PathVariable Long id
        , @RequestBody TeamDto.Request request) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!teamService.changeTeamInfo(team, request.getTeamName())) {
            throw new RuntimeException("팀 정보 변경에 실패했습니다.");
        }

        return new TeamDto.Response(team);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!teamService.deleteTeam(team)) {
            throw new RuntimeException("팀 삭제에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
