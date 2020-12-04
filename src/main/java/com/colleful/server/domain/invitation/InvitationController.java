package com.colleful.server.domain.invitation;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.security.JwtProvider;
import com.colleful.server.domain.team.TeamService;
import com.colleful.server.domain.user.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    private final InvitationService invitationService;
    private final JwtProvider provider;

    public InvitationController(UserService userService,
        TeamService teamService,
        InvitationService invitationService,
        JwtProvider provider) {
        this.userService = userService;
        this.teamService = teamService;
        this.invitationService = invitationService;
        this.provider = provider;
    }

    @GetMapping
    public List<InvitationDto.Response> getAllInvitations(@RequestHeader("Access-Token") String token) {
        User user = userService.getUserInfo(provider.getId(token))
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        List<Invitation> invitations = invitationService.getAllInvitations(user);
        List<InvitationDto.Response> responses = new ArrayList<>();
        for (Invitation invitation : invitations) {
            responses.add(new InvitationDto.Response(invitation));
        }

        return responses;
    }

    @PostMapping("/{team-id}/{user-id}")
    public ResponseEntity<?> createMember(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("team-id") Long teamId, @PathVariable("user-id") Long userId) {
        Team team = teamService.getTeamInfo(teamId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (user.getTeam().getId().equals(team.getId())) {
            throw new ForbiddenBehaviorException("이미 가입된 유저입니다.");
        }

        if (invitationService.alreadyInvited(team, user)) {
            throw new ForbiddenBehaviorException("이미 초대했습니다.");
        }

        if (team.isDifferentGender(user.getGender())) {
            throw new ForbiddenBehaviorException("같은 성별만 초대할 수 있습니다.");
        }

        if (team.isNotLeader(provider.getId(token))) {
            throw new ForbiddenBehaviorException("리더만 초대할 수 있습니다.");
        }

        Invitation invitation = new Invitation(team, user);
        if (!invitationService.invite(invitation)) {
            throw new RuntimeException("초대에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{id}/accept")
    public ResponseEntity<?> acceptInvitation(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        Invitation invitation = invitationService.getInvitation(id)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));

        if (invitation.isNotForMe(provider.getId(token))) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        if (!invitationService.endInvitation(id)) {
            throw new RuntimeException("초대 수락에 실패했습니다.");
        }

        if (!userService.joinTeam(invitation.getUser(), invitation.getTeam())) {
            throw new RuntimeException("초대 수락에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/refuse")
    public ResponseEntity<?> refuseInvitation(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        Invitation invitation = invitationService.getInvitation(id)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));

        if (invitation.isNotForMe(provider.getId(token))) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        if (!invitationService.endInvitation(id)) {
            throw new RuntimeException("초대 거절에 실패했습니다.");
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
