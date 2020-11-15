package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMatchRequest;
import com.ocupid.server.domain.TeamStatus;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.TeamDto.MatchResponse;
import com.ocupid.server.exception.ForbiddenBehaviorException;
import com.ocupid.server.exception.NotFoundResourceException;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.TeamMatchRequestService;
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
public class MatchRequestController {

    private final UserService userService;
    private final TeamService teamService;
    private final TeamMatchRequestService teamMatchRequestService;
    private final JwtProvider provider;

    public MatchRequestController(UserService userService,
        TeamService teamService,
        TeamMatchRequestService teamMatchRequestService,
        JwtProvider provider) {
        this.userService = userService;
        this.teamService = teamService;
        this.teamMatchRequestService = teamMatchRequestService;
        this.provider = provider;
    }

    @GetMapping
    public List<MatchResponse> getAllMatchRequests(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
                .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        List<TeamMatchRequest> matches = teamMatchRequestService.getAllMatchRequests(user);
        List<MatchResponse> responses = new ArrayList<>();
        for (TeamMatchRequest match : matches) {
            responses.add(new MatchResponse(match));
        }

        return responses;
    }

    @PostMapping("{sender-id}/{receiver-id}")
    public ResponseEntity<?> createMatchRequest(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("sender-id") Long senderId,
        @PathVariable("receiver-id") Long receiverId) {
        Team sender = teamService.getTeamInfo(senderId)
                .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        Team receiver = teamService.getTeamInfo(receiverId)
                .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));

        if (teamMatchRequestService.isAlreadyRequested(sender, receiver)) {
            throw new ForbiddenBehaviorException("이미 매칭 요청한 팀입니다.");
        }

        if (sender.getLeader().getGender().compareTo(receiver.getLeader().getGender()) == 0) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (!sender.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (!receiver.getStatus().equals(TeamStatus.READY)){
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        TeamMatchRequest match = new TeamMatchRequest(sender, receiver);
        if (!teamMatchRequestService.sendMatchRequest(match)) {
            throw new RuntimeException("매칭 요청에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/accept")
    public ResponseEntity<?> acceptMatchRequest(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        TeamMatchRequest match = teamMatchRequestService.getMatchRequest(id)
                .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        Team sender = match.getSender();
        Team receiver = match.getReceiver();

        if (!receiver.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        if (!teamMatchRequestService.endMatch(id)) {
            throw new RuntimeException("매칭 수락에 실패했습니다.");
        }

        if (!teamService.saveMatchInfo(sender, receiver)) {
            throw new RuntimeException("매칭 수락에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/refuse")
    public ResponseEntity<?> refuseMatchRequest(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        TeamMatchRequest match = teamMatchRequestService.getMatchRequest(id)
                .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        Team receiver = match.getReceiver();

        if (!receiver.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        if (!teamMatchRequestService.endMatch(id)) {
            throw new RuntimeException("매칭 거절에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}