package com.ocupid.server.service;

import com.ocupid.server.domain.TeamInvitation;
import com.ocupid.server.domain.User;
import com.ocupid.server.repository.TeamInvitationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeamInvitationService {

    TeamInvitationRepository teamInvitationRepository;

    public TeamInvitationService(
        TeamInvitationRepository teamInvitationRepository) {
        this.teamInvitationRepository = teamInvitationRepository;
    }

    public Boolean invite(TeamInvitation invitation) {
        try {
            teamInvitationRepository.save(invitation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<TeamInvitation> getAllInvitations(User user) {
        return teamInvitationRepository.findAllByUser(user);
    }
}
