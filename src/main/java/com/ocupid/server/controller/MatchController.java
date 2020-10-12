package com.ocupid.server.controller;

import com.ocupid.server.domain.*;
import com.ocupid.server.dto.TeamDto.MatchResponse;
import com.ocupid.server.exception.ForbiddenBehaviorException;
import com.ocupid.server.exception.NotFoundResourceException;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.TeamMatchService;
import com.ocupid.server.service.TeamMemberService;
import com.ocupid.server.service.TeamService;
import java.util.ArrayList;
import java.util.List;

import com.ocupid.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final UserService userService;
    private final TeamService teamService;
    private final TeamMatchService teamMatchService;
    private final TeamMemberService teamMemberService;
    private final JwtProvider provider;

    public MatchController(UserService userService,
                           TeamService teamService,
                           TeamMatchService teamMatchService,
                           TeamMemberService teamMemberService,
                           JwtProvider provider) {
        this.userService = userService;
        this.teamService = teamService;
        this.teamMatchService = teamMatchService;
        this.teamMemberService = teamMemberService;
        this.provider = provider;
    }

    @GetMapping
    public List<MatchResponse> getAllMatchRequests(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
                .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        TeamMember teamMember = teamMemberService.getTeamInfoByUser(user)
                .orElseThrow(() -> new NotFoundResourceException("팀에 속해있지 않은 유저입니다."));

        Team team = teamMember.getTeam();

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 팀에게 온 매칭 신청을 확인할 수 있습니다.");
        }

        List<TeamMatch> matches = teamMatchService.getAllMatchRequests(team);
        List<MatchResponse> responses = new ArrayList<>();
        for (TeamMatch match : matches) {
            responses.add(new MatchResponse(match));
        }

        return responses;
    }

    @PostMapping("{teamSend-id}/{teamReceive-id}")
    public ResponseEntity<?> createMatchRequest(@RequestHeader(value = "Access-Token") String token,
                                          @PathVariable("teamSend-id") Long teamIdSend, @PathVariable("teamReceive-id") Long teamIdReceive) {
        Team teamSend = teamService.getTeamInfo(teamIdSend)
                .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        Team teamReceive = teamService.getTeamInfo(teamIdReceive)
                .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));

        if (teamMatchService.alreadyRequestedMatch(teamSend, teamReceive)) {
            throw new ForbiddenBehaviorException("이미 매칭 신청한 팀입니다.");
        }

        if (teamSend.getLeader().getGender().compareTo(teamReceive.getLeader().getGender()) == 0) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 신청할 수 있습니다.");
        }

        if (!teamSend.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 신청 할 수 있습니다.");
        }

        if (!teamReceive.getStatus().equals(TeamStatus.READY)){
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 신청할 수 있습니다.");
        }

        TeamMatch match = new TeamMatch(teamSend, teamReceive);
        if (!teamMatchService.sendMatchRequest(match)) {
            throw new RuntimeException("매칭 신청에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/accept")
    public ResponseEntity<?> acceptMatchRequest(@RequestHeader("Access-Token") String token,
                                              @PathVariable Long id){
        TeamMatch match = teamMatchService.getMatchRequest(id)
                .orElseThrow(() -> new NotFoundResourceException("매칭 신청 정보가 없습니다."));

        Team teamSend = match.getTeamSend();
        Team teamReceive = match.getTeamReceive();

        if (!teamReceive.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        if (!teamMatchService.endMatch(id)) {
            throw new RuntimeException("매칭 수락에 실패했습니다.");
        }

        if (!teamService.saveMatchInfo(teamSend,teamReceive)) {
            throw new RuntimeException("매칭 수락에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/refuse")
    public ResponseEntity<?> declineMatchRequest(@RequestHeader("Access-Token") String token,
                                               @PathVariable Long id) {
        TeamMatch match = teamMatchService.getMatchRequest(id)
                .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        Team teamReceive = match.getTeamReceive();

        if (!teamReceive.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        if (!teamMatchService.endMatch(id)) {
            throw new RuntimeException("매칭 요청 거절에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
