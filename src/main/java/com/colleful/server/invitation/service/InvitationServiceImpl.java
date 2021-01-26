package com.colleful.server.invitation.service;

import com.colleful.server.invitation.domain.Invitation;
import com.colleful.server.invitation.repository.InvitationRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.service.TeamService;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamService teamService;
    private final UserService userService;

    @Override
    public Long invite(Long targetId, Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("팀을 먼저 생성해주세요.");
        }

        Team team = teamService.getTeam(user.getTeamId());

        if (!team.isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 초대할 수 있습니다.");
        }

        User targetUser = userService.getUser(targetId);

        if (invitationRepository.existsByTeamAndUser(team, targetUser)) {
            throw new ForbiddenBehaviorException("이미 초대했습니다.");
        }

        Invitation invitation = new Invitation(team, targetUser);
        invitationRepository.save(invitation);
        return invitation.getId();
    }

    @Override
    public List<Invitation> getAllInvitationsToMe(Long userId) {
        User user = userService.getUser(userId);
        return invitationRepository.findAllByUser(user);
    }

    @Override
    public List<Invitation> getAllInvitationsFromMyTeam(Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("팀을 먼저 생성해 주세요.");
        }

        Team team = teamService.getTeam(user.getTeamId());

        return invitationRepository.findAllByTeam(team);
    }

    @Override
    public void accept(Long invitationId, Long userId) {
        Invitation invitation = getInvitation(invitationId);

        if (invitation.isNotForMe(userId)) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        invitation.accept();

        invitationRepository.deleteAllByUser(invitation.getUser());
    }

    @Override
    public void refuse(Long invitationId, Long userId) {
        Invitation invitation = getInvitation(invitationId);

        if (invitation.isNotForMe(userId)) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        invitationRepository.deleteById(invitationId);
    }

    @Override
    public void cancel(Long invitationId, Long userId) {
        Invitation invitation = getInvitation(invitationId);

        if (!invitation.getTeam().isLedBy(userId)) {
            throw new ForbiddenBehaviorException("취소 권한이 없습니다.");
        }

        invitationRepository.deleteById(invitationId);
    }

    private Invitation getInvitation(Long id) {
        return invitationRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));
    }
}
