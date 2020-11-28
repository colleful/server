package com.colleful.server.service;

import com.colleful.server.repository.TeamMatchRequestRepository;
import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamMatchRequest;
import com.colleful.server.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TeamMatchRequestService {

    TeamMatchRequestRepository teamMatchRequestRepository;

    public TeamMatchRequestService(
            TeamMatchRequestRepository teamMatchRequestRepository) {
        this.teamMatchRequestRepository = teamMatchRequestRepository;
    }

    public Boolean sendMatchRequest(TeamMatchRequest match) {
        try {
            teamMatchRequestRepository.save(match);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<TeamMatchRequest> getMatchRequest(Long id) {
        return teamMatchRequestRepository.findById(id);
    }

    public List<TeamMatchRequest> getAllMatchRequests(User leader) {
        return teamMatchRequestRepository.findAllByReceiver_LeaderId(leader.getId());
    }

    public boolean isAlreadyRequested(Team sender, Team receiver) {
        return teamMatchRequestRepository.existsBySenderAndReceiver(sender, receiver);
    }

    public Boolean endMatch(Long id) {
        try {
            teamMatchRequestRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
