package com.colleful.server.domain.invitation;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {

    InvitationRepository invitationRepository;

    public InvitationService(
        InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public Boolean invite(Invitation invitation) {
        try {
            invitationRepository.save(invitation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Invitation> getInvitation(Long id) {
        return invitationRepository.findById(id);
    }

    public List<Invitation> getAllInvitations(User user) {
        return invitationRepository.findAllByUser(user);
    }

    public Boolean endInvitation(Long id) {
        try {
            invitationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean alreadyInvited(Team team, User user) {
        return invitationRepository.existsByTeamAndUser(team, user);
    }
}
