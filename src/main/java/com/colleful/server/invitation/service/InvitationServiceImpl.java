package com.colleful.server.invitation.service;

import com.colleful.server.invitation.domain.Invitation;
import com.colleful.server.invitation.repository.InvitationRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.service.TeamServiceForOtherService;
import com.colleful.server.user.domain.User;
import com.colleful.server.user.service.UserServiceForOtherService;
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
    private final TeamServiceForOtherService teamService;
    private final UserServiceForOtherService userService;

    @Override
    public Invitation invite(Long clientId, Long targetId) {
        Team team = teamService.getUserTeam(clientId);
        User targetUser = userService.getUserIfExist(targetId);

        if (team.isNotLedBy(clientId)) {
            throw new ForbiddenBehaviorException("리더만 초대할 수 있습니다.");
        }

        if (invitationRepository.existsByTeamAndUser(team, targetUser)) {
            throw new ForbiddenBehaviorException("이미 초대했습니다.");
        }

        return invitationRepository.save(new Invitation(team, targetUser));
    }

    @Override
    public List<Invitation> getAllSentInvitations(Long clientId) {
        Team team = teamService.getUserTeam(clientId);
        return invitationRepository.findAllByTeam(team);
    }

    @Override
    public List<Invitation> getAllReceivedInvitations(Long clientId) {
        User user = userService.getUserIfExist(clientId);
        return invitationRepository.findAllByUser(user);
    }

    @Override
    public void accept(Long clientId, Long invitationId) {
        Invitation invitation = getInvitationIfExist(invitationId);

        if (invitation.isNotReceivedBy(clientId)) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        invitation.accept();

        invitationRepository.deleteAllByUser(invitation.getUser());
    }

    @Override
    public void refuse(Long clientId, Long invitationId) {
        Invitation invitation = getInvitationIfExist(invitationId);

        if (invitation.isNotReceivedBy(clientId)) {
            throw new ForbiddenBehaviorException("잘못된 유저입니다.");
        }

        invitationRepository.deleteById(invitationId);
    }

    @Override
    public void cancel(Long clientId, Long invitationId) {
        Invitation invitation = getInvitationIfExist(invitationId);

        if (invitation.isNotSentBy(clientId)) {
            throw new ForbiddenBehaviorException("취소 권한이 없습니다.");
        }

        invitationRepository.deleteById(invitationId);
    }

    private Invitation getInvitationIfExist(Long id) {
        return invitationRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("초대 정보가 없습니다."));
    }
}
