package com.ocupid.server.controller;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamInvitation;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.User;
import com.ocupid.server.dto.UserDto.InvitationResponse;
import com.ocupid.server.exception.ForbiddenBehaviorException;
import com.ocupid.server.exception.NotFoundResourceException;
import com.ocupid.server.security.JwtProvider;
import com.ocupid.server.service.TeamInvitationService;
import com.ocupid.server.service.TeamMemberService;
import com.ocupid.server.service.TeamService;
import com.ocupid.server.service.UserService;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
public class InvitationController {

    private final UserService userService;
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final TeamInvitationService teamInvitationService;
    private final JwtProvider provider;

    public InvitationController(UserService userService,
        TeamService teamService, TeamMemberService teamMemberService,
        TeamInvitationService teamInvitationService,
        JwtProvider provider) {
        this.userService = userService;
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
        this.teamInvitationService = teamInvitationService;
        this.provider = provider;
    }

    @GetMapping
    public List<InvitationResponse> getAllInvitations(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        List<TeamInvitation> invitations = teamInvitationService.getAllInvitations(user);
        List<InvitationResponse> responses = new ArrayList<>();
        for (TeamInvitation invitation : invitations) {
            responses.add(new InvitationResponse(invitation));
        }

        return responses;
    }

    @PostMapping("{team-id}/{user-id}")
    public ResponseEntity<?> createMember(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("team-id") Long teamId, @PathVariable("user-id") Long userId) {
        Team team = teamService.getTeamInfo(teamId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (teamMemberService.alreadyJoined(team, user)) {
            throw new ForbiddenBehaviorException("이미 가입된 유저입니다.");
        }

        if (teamInvitationService.alreadyInvited(team, user)) {
            throw new ForbiddenBehaviorException("이미 초대했습니다.");
        }

        if (team.isDifferentGender(user.getGender())) {
            throw new ForbiddenBehaviorException("같은 성별만 초대할 수 있습니다.");
        }

        if (!team.getLeader().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 초대할 수 있습니다.");
        }

        TeamInvitation invitation = new TeamInvitation(team, user);
        if (!teamInvitationService.invite(invitation)) {
            throw new RuntimeException("초대에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/accept")
    public ResponseEntity<?> acceptInvitation(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        TeamInvitation invitation = teamInvitationService.getInvitation(id)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));

        if (!invitation.getUser().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        if (!teamInvitationService.endInvitation(id)) {
            throw new RuntimeException("초대 수락에 실패했습니다.");
        }

        TeamMember member = new TeamMember(invitation.getTeam(), invitation.getUser());
        if (!teamMemberService.addMember(member)) {
            throw new RuntimeException("초대 수락에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/refuse")
    public ResponseEntity<?> refuseInvitation(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        TeamInvitation invitation = teamInvitationService.getInvitation(id)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));

        if (!invitation.getUser().getId().equals(provider.getId(token))) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        if (!teamInvitationService.endInvitation(id)) {
            throw new RuntimeException("초대 거절에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
