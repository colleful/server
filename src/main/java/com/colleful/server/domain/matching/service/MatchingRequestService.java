package com.colleful.server.domain.matching.service;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.repository.MatchingRequestRepository;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.service.TeamService;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingRequestService {

    MatchingRequestRepository matchingRequestRepository;
    TeamService teamService;
    UserService userService;

    @Transactional
    public void sendMatchRequest(Long senderId, Long receiverId, Long userId) {
        Team sender = teamService.getTeamInfo(senderId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        Team receiver = teamService.getTeamInfo(receiverId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));

        if (isAlreadyRequested(senderId, receiverId)) {
            throw new ForbiddenBehaviorException("이미 매칭 요청한 팀입니다.");
        }

        if (!sender.isDifferentGender(receiver.getGender())) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (sender.isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 요청 할 수 있습니다.");
        }

        if (receiver.isNotReady()){
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        MatchingRequest match = new MatchingRequest(sender, receiver);
        matchingRequestRepository.save(match);
    }

    @Transactional
    public List<MatchingRequest> getAllMatchRequests(Long userId) {
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        if (user.getTeamId() == null) {
            return new ArrayList<>();
        }

        Team team = teamService.getTeamInfo(user.getTeamId())
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        if (team.isNotLeader(userId)) {
            return new ArrayList<>();
        }

        return matchingRequestRepository.findAllByReceiver(team);
    }

    public boolean isAlreadyRequested(Long senderId, Long receiverId) {
        Team sender = teamService.getTeamInfo(senderId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        Team receiver = teamService.getTeamInfo(receiverId)
            .orElseThrow(() -> new NotFoundResourceException("생성되지 않은 팀입니다."));
        return matchingRequestRepository.existsBySenderAndReceiver(sender, receiver);
    }

    @Transactional
    public void accept(Long matchId, Long userId) {
        MatchingRequest match = matchingRequestRepository.findById(matchId)
            .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        if (match.getReceiver().isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 수락할 수 있습니다.");
        }

        teamService.saveMatchInfo(match.getSender().getId(), match.getReceiver().getId());
        deleteMatchInfo(matchId);
    }

    @Transactional
    public void refuse(Long matchId, Long userId) {
        MatchingRequest match = matchingRequestRepository.findById(matchId)
            .orElseThrow(() -> new NotFoundResourceException("매칭 요청 정보가 없습니다."));

        if (match.getReceiver().isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭 거절할 수 있습니다.");
        }

        deleteMatchInfo(matchId);
    }

    public void deleteMatchInfo(Long id) {
        matchingRequestRepository.deleteById(id);
    }
}
