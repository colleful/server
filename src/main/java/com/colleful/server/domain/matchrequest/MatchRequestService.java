package com.colleful.server.domain.matchrequest;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MatchRequestService {

    MatchRequestRepository matchRequestRepository;

    public MatchRequestService(
            MatchRequestRepository matchRequestRepository) {
        this.matchRequestRepository = matchRequestRepository;
    }

    public Boolean sendMatchRequest(MatchRequest match) {
        try {
            matchRequestRepository.save(match);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<MatchRequest> getMatchRequest(Long id) {
        return matchRequestRepository.findById(id);
    }

    public List<MatchRequest> getAllMatchRequests(User leader) {
        return matchRequestRepository.findAllByReceiver_LeaderId(leader.getId());
    }

    public boolean isAlreadyRequested(Team sender, Team receiver) {
        return matchRequestRepository.existsBySenderAndReceiver(sender, receiver);
    }

    public Boolean endMatch(Long id) {
        try {
            matchRequestRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
