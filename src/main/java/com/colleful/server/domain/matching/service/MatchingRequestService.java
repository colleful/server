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
public class MatchingRequestService {

    private final MatchingRequestRepository matchingRequestRepository;
    private final TeamService teamService;
    private final UserService userService;

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

        if (!sender.isDifferentGender(receiver.getGender())) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (!sender.isLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (receiver.isNotReady()) {
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        MatchingRequest match = new MatchingRequest(sender, receiver);
        matchingRequestRepository.save(match);
        return match.getId();
    }

    public MatchingRequest getMatchingRequest(Long id) {
        return matchingRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("매칭 요청이 없습니다."));
    }

    public List<MatchingRequest> getAllMatchRequests(Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("먼저 팀에 가입해주세요.");
        }

        Team team = teamService.getTeam(user.getTeamId());

        if (!team.isLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 조회할 수 있습니다.");
        }

        return matchingRequestRepository.findAllByReceiver(team);
    }

    public void accept(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (!match.getReceiver().isLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        match.accept();
        matchingRequestRepository.deleteById(matchingId);
    }

    public void refuse(Long matchingId, Long userId) {
        MatchingRequest match = getMatchingRequest(matchingId);

        if (!match.getReceiver().isLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        matchingRequestRepository.deleteById(matchingId);
    }
}
