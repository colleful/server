package com.colleful.server.invitation.service;

import com.colleful.server.invitation.domain.Invitation;
import java.util.List;

public interface InvitationService {

    Invitation invite(Long clientId, Long targetId);

    List<Invitation> getAllSentInvitations(Long clientId);

    List<Invitation> getAllReceivedInvitations(Long clientId);

    void accept(Long clientId, Long invitationId);

    void refuse(Long clientId, Long invitationId);

    void cancel(Long clientId, Long invitationId);
}
