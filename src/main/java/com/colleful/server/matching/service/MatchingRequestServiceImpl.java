package com.colleful.server.matching.service;

import com.colleful.server.global.exception.ErrorType;
import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.matching.repository.MatchingRequestRepository;
import com.colleful.server.team.domain.Team;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.team.service.TeamServiceForOtherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingRequestServiceImpl implements MatchingRequestService {

    private final MatchingRequestRepository matchingRequestRepository;
    private final TeamServiceForOtherService teamService;

    @Override
    public MatchingRequest request(Long senderId, Long receivedTeamId) {
        Team sentTeam = teamService.getUserTeam(senderId);
        Team receivedTeam = teamService.getTeamIfExist(receivedTeamId);

        if (sentTeam.isNotLedBy(senderId)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_LEADER);
        }

        if (matchingRequestRepository.existsBySentTeamAndReceivedTeam(sentTeam, receivedTeam)) {
            throw new ForbiddenBehaviorException(ErrorType.ALREADY_REQUESTED);
        }

        return matchingRequestRepository.save(new MatchingRequest(sentTeam, receivedTeam));
    }

    @Override
    public List<MatchingRequest> getAllSentMatchingRequests(Long clientId) {
        Team team = teamService.getUserTeam(clientId);
        return matchingRequestRepository.findAllBySentTeam(team);
    }

    @Override
    public List<MatchingRequest> getAllReceivedMatchingRequests(Long clientId) {
        Team team = teamService.getUserTeam(clientId);
        return matchingRequestRepository.findAllByReceivedTeam(team);
    }

    @Override
    public void accept(Long clientId, Long matchingId) {
        MatchingRequest match = getMatchingRequestIfExist(matchingId);

        if (match.isNotReceivedBy(clientId)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_LEADER);
        }

        match.accept();
        matchingRequestRepository.deleteAllByReceivedTeam(match.getReceivedTeam());
    }

    @Override
    public void refuse(Long clientId, Long matchingId) {
        MatchingRequest match = getMatchingRequestIfExist(matchingId);

        if (match.isNotReceivedBy(clientId)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_LEADER);
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    @Override
    public void cancel(Long clientId, Long matchingId) {
        MatchingRequest match = getMatchingRequestIfExist(matchingId);

        if (match.isNotSentBy(clientId)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_LEADER);
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    private MatchingRequest getMatchingRequestIfExist(Long id) {
        return matchingRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException(ErrorType.NOT_FOUND_MATCHING_REQUEST));
    }
}
