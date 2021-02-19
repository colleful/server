package com.colleful.server.matching.service;

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
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (matchingRequestRepository.existsBySentTeamAndReceivedTeam(sentTeam, receivedTeam)) {
            throw new ForbiddenBehaviorException("이미 매칭 요청한 팀입니다.");
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
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        match.accept();
        matchingRequestRepository.deleteAllByReceivedTeam(match.getReceivedTeam());
    }

    @Override
    public void refuse(Long clientId, Long matchingId) {
        MatchingRequest match = getMatchingRequestIfExist(matchingId);

        if (match.isNotReceivedBy(clientId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    @Override
    public void cancel(Long clientId, Long matchingId) {
        MatchingRequest match = getMatchingRequestIfExist(matchingId);

        if (match.isNotSentBy(clientId)) {
            throw new ForbiddenBehaviorException("취소 권한이 없습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    private MatchingRequest getMatchingRequestIfExist(Long id) {
        return matchingRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("매칭 요청이 없습니다."));
    }
}
