package com.colleful.server.invitation.service;

import com.colleful.server.invitation.domain.Invitation;
import java.util.List;

public interface InvitationService {

    Invitation invite(Long targetId, Long userId);

    List<Invitation> getAllSentInvitations(Long userId);

    List<Invitation> getAllReceivedInvitations(Long userId);

    void accept(Long invitationId, Long userId);

    void refuse(Long invitationId, Long userId);

    void cancel(Long invitationId, Long userId);
}
