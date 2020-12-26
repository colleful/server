package com.colleful.server.domain.invitation.service;

import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.domain.invitation.repository.InvitationRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamService teamService;
    private final UserService userService;

    public void invite(Long teamId, Long targetId, Long userId) {
        Team team = teamService.getTeamInfo(teamId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        User targetUser = userService.getUserInfo(targetId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (targetUser.getTeamId() != null) {
            throw new ForbiddenBehaviorException("이미 팀에 가입된 유저입니다.");
        }

        if (alreadyInvited(teamId, targetId)) {
            throw new ForbiddenBehaviorException("이미 초대했습니다.");
        }

        if (team.isDifferentGender(targetUser.getGender())) {
            throw new ForbiddenBehaviorException("같은 성별만 초대할 수 있습니다.");
        }

        if (team.isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 초대할 수 있습니다.");
        }

        Invitation invitation = new Invitation(team, targetUser);
        invitationRepository.save(invitation);
    }

    public List<Invitation> getAllInvitations(Long userId) {
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        return invitationRepository.findAllByUser(user);
    }

    public void accept(Long invitationId, Long userId) {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));

        if (invitation.isNotForMe(userId)) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        teamService.joinTeam(invitation.getUser().getId(), invitation.getTeam().getId());
        invitationRepository.deleteById(invitationId);
    }

    public void refuse(Long invitationId, Long userId) {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));

        if (invitation.isNotForMe(userId)) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        invitationRepository.deleteById(invitationId);
    }

    public boolean alreadyInvited(Long teamId, Long userId) {
        Team team = teamService.getTeamInfo(teamId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        return invitationRepository.existsByTeamAndUser(team, user);
    }
}
