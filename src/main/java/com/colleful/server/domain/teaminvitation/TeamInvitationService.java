package com.colleful.server.domain.teaminvitation;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import java.util.List;
import java.util.Optional;
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

    public Optional<TeamInvitation> getInvitation(Long id) {
        return teamInvitationRepository.findById(id);
    }

    public List<TeamInvitation> getAllInvitations(User user) {
        return teamInvitationRepository.findAllByUser(user);
    }

    public Boolean endInvitation(Long id) {
        try {
            teamInvitationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean alreadyInvited(Team team, User user) {
        return teamInvitationRepository.existsByTeamAndUser(team, user);
    }
}
