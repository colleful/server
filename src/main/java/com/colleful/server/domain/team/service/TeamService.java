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
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;

    @Transactional
    public Long createTeam(TeamDto.Request dto, Long leaderId) {
        User leader = userService.getUserInfo(leaderId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        Team team = dto.toEntity(leader);
        teamRepository.save(team);
        userService.joinTeam(team.getLeaderId(), team.getId());
        return team.getId();
    }

    public Page<Team> getAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    public Optional<Team> getTeamInfo(Long id) {
        return teamRepository.findById(id);
    }

    public Team getTeamInfo(Long teamId, Long userId) {
        User user = userService.getUserInfo(userId)
            .orElseThrow(() -> new NotFoundResourceException("가입되지 않은 유저입니다."));
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotReady() || !user.getTeamId().equals(teamId)) {
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

    public void changeTeamInfo(Long teamId, String teamName) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
        team.changeTeamName(teamName);
        teamRepository.save(team);
    }

    public void updateTeamStatus(Long teamId, Long userId, TeamStatus status) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.isNotLeader(userId)) {
            throw new ForbiddenBehaviorException("리더만 팀 상태를 변경할 수 있습니다.");
        }

        team.changeStatus(status);
        teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        clearMatch(teamId);
        List<User> users = userService.getMembers(teamId);
        users.forEach(user -> userService.leaveTeam(user.getId()));
        teamRepository.deleteById(teamId);
    }

    public void clearMatch(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));

        if (team.getMatchedTeamId() != null) {
            Team matchedTeam = teamRepository.findById(team.getMatchedTeamId())
                .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
            team.endMatch();
            matchedTeam.endMatch();
            teamRepository.save(team);
            teamRepository.save(matchedTeam);
        }
    }

    @Transactional
    public void saveMatchInfo(Long senderId, Long receiverId){
        Team sender = teamRepository.findById(senderId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
        Team receiver = teamRepository.findById(receiverId)
            .orElseThrow(() -> new NotFoundResourceException("팀이 존재하지 않습니다."));
        sender.match(receiverId);
        receiver.match(senderId);
        teamRepository.save(sender);
        teamRepository.save(receiver);
    }
}
