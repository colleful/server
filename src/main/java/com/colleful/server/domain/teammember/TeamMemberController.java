package com.colleful.server.domain.teammember;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.team.TeamDto;
import com.colleful.server.domain.team.TeamService;
import com.colleful.server.domain.user.User;
import com.colleful.server.domain.user.UserDto;
import com.colleful.server.domain.user.UserDto.Response;
import com.colleful.server.domain.user.UserService;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.security.JwtProvider;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final UserService userService;
    private final JwtProvider provider;

    public TeamMemberController(
        TeamMemberService teamMemberService,
        TeamService teamService, UserService userService,
        JwtProvider provider) {
        this.teamMemberService = teamMemberService;
        this.teamService = teamService;
        this.userService = userService;
        this.provider = provider;
    }

    @GetMapping("/teams/{team-id}/members")
    public List<UserDto.Response> getAllMembers(@PathVariable("team-id") Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
        List<TeamMember> members = teamMemberService.getMemberInfoByTeam(team);
        return members.stream()
            .map(member -> new Response(member.getMember()))
            .collect(Collectors.toList());
    }

    @GetMapping("/users/{user-id}/teams")
    public List<TeamDto.Response> getAllTeams(@PathVariable("user-id") Long id) {
        User user = userService.getUserInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        List<TeamMember> members = teamMemberService.getTeamInfoByUser(user);
        return members.stream()
            .map(member -> new TeamDto.Response(member.getTeam()))
            .collect(Collectors.toList());
    }

    @DeleteMapping("/teams/{team-id}/members")
    public ResponseEntity<?> leaveTeam(@RequestHeader("Access-Token") String token,
        @PathVariable("team-id") Long id) {
        Team team = teamService.getTeamInfo(id)
            .orElseThrow(() -> new NotFoundResourceException("팀 정보가 없습니다."));
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (!teamMemberService.leaveTeam(team, user)) {
            throw new RuntimeException("팀 탈퇴에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
