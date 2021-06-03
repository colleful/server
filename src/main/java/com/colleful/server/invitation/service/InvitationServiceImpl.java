package com.colleful.server.invitation.service;

import com.colleful.server.global.exception.ErrorType;
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
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_LEADER);
        }

        if (invitationRepository.existsByTeamAndUser(team, targetUser)) {
            throw new ForbiddenBehaviorException(ErrorType.ALREADY_INVITED);
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
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_MY_INVITATION);
        }

        invitation.accept();
        invitationRepository.deleteAllByUser(invitation.getUser());
    }

    @Override
    public void refuse(Long clientId, Long invitationId) {
        Invitation invitation = getInvitationIfExist(invitationId);

        if (invitation.isNotReceivedBy(clientId)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_MY_INVITATION);
        }

        invitationRepository.deleteById(invitationId);
    }

    @Override
    public void cancel(Long clientId, Long invitationId) {
        Invitation invitation = getInvitationIfExist(invitationId);

        if (invitation.isNotSentBy(clientId)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_LEADER);
        }

        invitationRepository.deleteById(invitationId);
    }

    private Invitation getInvitationIfExist(Long id) {
        return invitationRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException(ErrorType.NOT_FOUND_INVITATION));
    }
}
