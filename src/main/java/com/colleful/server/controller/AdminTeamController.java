package com.colleful.server.controller;

import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamMember;
import com.colleful.server.dto.PageDto;
import com.colleful.server.dto.TeamDto.*;
import com.colleful.server.exception.NotFoundResourceException;
import com.colleful.server.service.TeamMemberService;
import com.colleful.server.service.TeamService;
import java.util.List;
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
    private final TeamMemberService teamMemberService;

    public AdminTeamController(TeamService teamService,
        TeamMemberService teamMemberService) {
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    public PageDto.Response<Response> getAllTeams(@PageableDefault Pageable request) {
        Page<Team> teams = teamService.getAllTeams(request);
        Page<Response> responses = teams.map(team -> {
            List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
            return new Response(team, members);
        });
        return new PageDto.Response<>(responses);
    }

    @PatchMapping("/{id}")
    public Response updateTeamInfo(@PathVariable Long id, @RequestBody Request request) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!teamService.changeTeamInfo(team, request.getTeamName())) {
            throw new RuntimeException("팀 정보 변경에 실패했습니다.");
        }

        List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
        return new Response(team, members);
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
