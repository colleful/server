package com.colleful.server.domain.matching.service;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingRequestServiceImpl implements MatchingRequestService {

    private final MatchingRequestRepository matchingRequestRepository;
    private final TeamService teamService;
    private final UserService userService;

    @Override
    public Long request(Long receiverId, Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("팀을 먼저 생성해 주세요.");
        }

        Team sender = teamService.getTeam(user.getTeamId());
        Team receiver = teamService.getTeam(receiverId);

        if (matchingRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new ForbiddenBehaviorException("이미 매칭 요청한 팀입니다.");
        }

        if (!sender.isDifferentGenderFrom(receiver.getGender())) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (!sender.isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (receiver.isNotReady()) {
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        MatchingRequest match = new MatchingRequest(sender, receiver);
        matchingRequestRepository.save(match);
        return match.getId();
    }

    @Override
    public List<MatchingRequest> getAllMatchingRequestsToMyTeam(Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("먼저 팀에 가입해주세요.");
        }

        Team team = teamService.getTeam(user.getTeamId());

        return matchingRequestRepository.findAllByReceiver(team);
    }

    @Override
    public List<MatchingRequest> getAllMatchingRequestsFromMyTeam(Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("먼저 팀에 가입해주세요.");
        }

        Team team = teamService.getTeam(user.getTeamId());

        return matchingRequestRepository.findAllBySender(team);
    }

    @Override
    public void accept(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (!match.getReceiver().isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        match.accept();
        deleteAllMatchingRequestsToMyTeam(match.getReceiver());
    }

    @Override
    public void refuse(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (!match.getReceiver().isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    @Override
    public void cancel(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (!match.getSender().isLedBy(userId)) {
            throw new ForbiddenBehaviorException("취소 권한이 없습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }

    private MatchingRequest getMatchingRequest(Long id) {
        return matchingRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("매칭 요청이 없습니다."));
    }

    private void deleteAllMatchingRequestsToMyTeam(Team team) {
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findAllByReceiver(team);
        matchingRequests.forEach(
            matchingRequest -> matchingRequestRepository.deleteById(matchingRequest.getId()));
    }
}
