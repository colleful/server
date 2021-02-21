package com.colleful.server.team.service;

import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.team.domain.Team;
import com.colleful.server.team.repository.TeamRepository;
import com.colleful.server.user.domain.User;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.user.service.UserServiceForOtherService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamServiceForController, TeamServiceForOtherService {

    private final TeamRepository teamRepository;
    private final UserServiceForOtherService userService;

    @Override
    public Team createTeam(Long clientId, String teamName) {
        User leader = userService.getUserIfExist(clientId);
        Team team = Team.of(teamName, leader);
        teamRepository.save(team);
        team.addMember(leader);
        return team;
    }

    @Override
    public Team getTeamIfExist(Long teamId) {
        return teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundResourceException("존재하지 않는 팀입니다."));
    }

    @Override
    public Team getTeam(Long clientId, Long teamId) {
        User client = userService.getUserIfExist(clientId);
        Team team = teamRepository.findById(teamId).orElseGet(Team::getEmptyInstance);

        if (team.isNotAccessibleTo(client)) {
            throw new ForbiddenBehaviorException("권한이 없습니다.");
        }

        return team;
    }

    @Override
    public Team getUserTeam(Long userId) {
        User user = userService.getUserIfExist(userId);

        if (user.hasNotTeam()) {
            throw new ForbiddenBehaviorException("팀을 먼저 생성해 주세요.");
        }

        return getTeamIfExist(user.getTeamId());
    }

    @Override
    public Page<Team> getAllReadyTeams(Pageable pageable) {
        return teamRepository.findAllByStatusOrderByUpdatedAtDesc(pageable, TeamStatus.READY);
    }

    @Override
    public List<User> getMembers(Long clientId, Long teamId) {
        User client = userService.getUserIfExist(clientId);
        Team team = getTeamIfExist(teamId);

        if (team.isNotAccessibleTo(client)) {
            throw new ForbiddenBehaviorException("권한이 없습니다.");
        }

        return userService.getMembers(teamId);
    }

    @Override
    public Page<Team> searchTeams(Pageable pageable, String teamName) {
        return teamRepository
            .findAllByStatusAndTeamNameContainingOrderByUpdatedAtDesc(pageable,
                TeamStatus.READY, teamName);
    }

    @Override
    public void changeStatus(Long clientId, TeamStatus status) {
        Team team = getUserTeam(clientId);

        if (team.isNotLedBy(clientId)) {
            throw new ForbiddenBehaviorException("리더만 팀 상태를 변경할 수 있습니다.");
        }

        team.changeStatus(status);
    }

    @Override
    public void leaveTeam(Long clientId) {
        User client = userService.getUserIfExist(clientId);
        Team team = getTeamIfExist(client.getTeamId());

        if (team.isLedBy(clientId)) {
            throw new ForbiddenBehaviorException("리더는 팀을 탈퇴할 수 없습니다.");
        }

        team.removeMember(client);
    }

    @Override
    public void deleteTeam(Long clientId) {
        Team team = getUserTeam(clientId);

        if (team.isNotLedBy(clientId)) {
            throw new ForbiddenBehaviorException("리더만 팀을 삭제할 수 있습니다.");
        }

        finishMatch(team);
        removeAllMembers(team);
        teamRepository.deleteById(team.getId());
    }

    @Override
    public void finishMatch(Long clientId) {
        Team team = getUserTeam(clientId);

        if (team.isLedBy(clientId)) {
            throw new ForbiddenBehaviorException("리더만 매칭을 끝낼 수 있습니다.");
        }

        if (team.isNotMatched()) {
            throw new ForbiddenBehaviorException("매칭된 팀이 없습니다.");
        }

        finishMatch(team);
    }

    private void finishMatch(Team team) {
        if (team.isMatched()) {
            Team matchedTeam = getTeamIfExist(team.getMatchedTeamId());
            team.finishMatch();
            matchedTeam.finishMatch();
        }
    }

    private void removeAllMembers(Team team) {
        List<User> users = userService.getMembers(team.getId());
        users.forEach(team::removeMember);
    }
}
