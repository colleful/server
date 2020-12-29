package com.colleful.server.domain.team.service;

import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.repository.TeamRepository;
import com.colleful.server.domain.user.domain.User;
import com.colleful.server.domain.user.service.UserService;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;

    public Long createTeam(TeamDto.Request dto, Long leaderId) {
        User leader = userService.getUserInfo(leaderId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        Team team = Team.builder()
            .teamName(dto.getTeamName())
            .gender(leader.getGender())
            .status(TeamStatus.PENDING)
            .leaderId(leaderId)
            .build();
        teamRepository.save(team);
        leader.joinTeam(team.getId());
        return team.getId();
    }

    public Optional<Team> getTeamInfo(Long id) {
        return teamRepository.findById(id);
    }

    public Team getTeamInfo(Long teamId, Long userId) {
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotReady() && !user.getTeamId().equals(teamId)) {
            throw new ForbiddenBehaviorException("권한이 없습니다.");
        }

        return team;
    }

    public Page<Team> getAllReadyTeams(Pageable pageable) {
        return teamRepository.findAllByStatusOrderByUpdatedAtDesc(pageable, TeamStatus.READY);
    }

    public Page<Team> searchTeams(Pageable pageable, String teamName) {
        return teamRepository
            .findAllByStatusAndTeamNameContainingOrderByUpdatedAtDesc(pageable,
                TeamStatus.READY, teamName);
    }

    public void updateTeamStatus(Long teamId, Long userId, TeamStatus status) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 팀 상태를 변경할 수 있습니다.");
        }

        team.changeStatus(status);
    }

    public void leaveTeam(Long userId) {
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        Team team = teamRepository.findById(user.getTeamId())
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (!team.isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더는 팀을 탈퇴할 수 없습니다.");
        }

        user.leaveTeam();
    }

    public void deleteTeam(Long userId) {
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));

        if (user.getTeamId() == null) {
            throw new ForbiddenBehaviorException("팀이 없습니다.");
        }

        Team team = teamRepository.findById(user.getTeamId())
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 팀을 삭제할 수 있습니다.");
        }

        clearMatch(team);
        List<User> users = userService.getMembers(user.getTeamId());
        teamRepository.deleteById(user.getTeamId());
        users.forEach(User::leaveTeam);
    }

    public void clearMatch(Team team) {
        if (team.getMatchedTeamId() != null) {
            Team matchedTeam = teamRepository.findById(team.getMatchedTeamId())
                .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
            team.endMatch();
            matchedTeam.endMatch();
        }
    }

    public void saveMatchInfo(Long senderId, Long receiverId) {
        Team sender = teamRepository.findById(senderId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
        Team receiver = teamRepository.findById(receiverId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
        sender.match(receiverId);
        receiver.match(senderId);
    }
}
