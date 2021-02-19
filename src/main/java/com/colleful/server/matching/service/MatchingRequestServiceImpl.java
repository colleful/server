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
    public MatchingRequest request(Long receiverId, Long userId) {
        Team sender = teamService.getUserTeam(userId);
        Team receiver = teamService.getTeamIfExist(receiverId);

        if (sender.isNotLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (matchingRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new ForbiddenBehaviorException("이미 매칭 요청한 팀입니다.");
        }

        return matchingRequestRepository.save(new MatchingRequest(sender, receiver));
    }

    @Override
    public List<MatchingRequest> getAllSentMatchingRequests(Long userId) {
        Team team = teamService.getUserTeam(userId);
        return matchingRequestRepository.findAllBySender(team);
    }

    @Override
    public List<MatchingRequest> getAllReceivedMatchingRequests(Long userId) {
        Team team = teamService.getUserTeam(userId);
        return matchingRequestRepository.findAllByReceiver(team);
    }

    @Override
    public void accept(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (match.isNotReceivedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        match.accept();

        matchingRequestRepository.deleteAllByReceiver(match.getReceiver());
    }

    @Override
    public void refuse(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (match.isNotReceivedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    @Override
    public void cancel(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (match.isNotSentBy(userId)) {
            throw new ForbiddenBehaviorException("취소 권한이 없습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    private MatchingRequest getMatchingRequest(Long id) {
        return matchingRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("매칭 요청이 없습니다."));
    }
}
