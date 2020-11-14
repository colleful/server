package com.ocupid.server.service;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMatchRequest;
import com.ocupid.server.domain.User;
import com.ocupid.server.repository.TeamMatchRequestRepository;
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
        return teamMatchRequestRepository.findAllByReceiver_Leader(leader);
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
