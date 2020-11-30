package com.colleful.server.domain.matchrequest;

import com.colleful.server.domain.team.Team;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.domain.user.User;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.security.JwtProvider;
import com.colleful.server.domain.team.TeamService;
import java.util.ArrayList;
import java.util.List;
import com.colleful.server.domain.user.UserService;
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
    private final MatchRequestService matchRequestService;
    private final JwtProvider provider;

    public MatchRequestController(UserService userService,
        TeamService teamService,
        MatchRequestService matchRequestService,
        JwtProvider provider) {
        this.userService = userService;
        this.teamService = teamService;
        this.matchRequestService = matchRequestService;
        this.provider = provider;
    }

    @GetMapping
    public List<MatchDto.Response> getAllMatchRequests(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
                .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        List<MatchRequest> matches = matchRequestService.getAllMatchRequests(user);
        List<MatchDto.Response> responses = new ArrayList<>();
        for (MatchRequest match : matches) {
            responses.add(new MatchDto.Response(match));
        }

        return responses;
    }

    @PostMapping("/{sender-id}/{receiver-id}")
    public ResponseEntity<?> createMatchRequest(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("sender-id") Long senderId,
        @PathVariable("receiver-id") Long receiverId) {
        Team sender = teamService.getTeamInfo(senderId)
                .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        Team receiver = teamService.getTeamInfo(receiverId)
                .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));

        if (matchRequestService.isAlreadyRequested(sender, receiver)) {
            throw new ForbiddenBehaviorException("이미 매칭 요청한 팀입니다.");
        }

        if (!sender.isDifferentGender(receiver.getGender())) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (sender.isNotLeader(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (receiver.isNotReady()){
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        MatchRequest match = new MatchRequest(sender, receiver);
        if (!matchRequestService.sendMatchRequest(match)) {
            throw new RuntimeException("매칭 요청에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/accept")
    public ResponseEntity<?> acceptMatchRequest(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        MatchRequest match = matchRequestService.getMatchRequest(id)
                .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        Team sender = match.getSender();
        Team receiver = match.getReceiver();

        if (receiver.isNotLeader(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        if (!matchRequestService.endMatch(id)) {
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
        MatchRequest match = matchRequestService.getMatchRequest(id)
                .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        Team receiver = match.getReceiver();

        if (receiver.isNotLeader(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        if (!matchRequestService.endMatch(id)) {
            throw new RuntimeException("매칭 거절에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
