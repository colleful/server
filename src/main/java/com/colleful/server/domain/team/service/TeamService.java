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
        User leader = userService.getUser(leaderId);

        if (leader.hasTeam()) {
            throw new ForbiddenBehaviorException("이미 팀에 가입되어 있습니다.");
        }

        Team team = Team.builder()
            .teamName(dto.getTeamName())
            .gender(leader.getGender())
            .status(TeamStatus.PENDING)
            .headcount(0)
            .leaderId(leaderId)
            .build();
        teamRepository.save(team);

        team.addMember(leader);

        return team.getId();
    }

    public Team getTeam(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourceException("존재하지 않는 팀입니다."));
    }

    public Team getTeam(Long teamId, Long userId) {
        User user = userService.getUser(userId);
        Team team = getTeam(teamId);

        if (team.isNotReady() && user.isNotMemberOf(teamId)) {
            throw new ForbiddenBehaviorException("권한이 없습니다.");
        }

        return team;
    }

    public Page<Team> getAllReadyTeams(Pageable pageable) {
        return teamRepository.findAllByStatusOrderByUpdatedAtDesc(pageable, TeamStatus.READY);
    }

    public List<User> getMembers(Long teamId) {
        return userService.getMembers(teamId);
    }

    public Page<Team> searchTeams(Pageable pageable, String teamName) {
        return teamRepository
            .findAllByStatusAndTeamNameContainingOrderByUpdatedAtDesc(pageable,
                TeamStatus.READY, teamName);
    }

    public void updateStatus(Long teamId, Long userId, TeamStatus status) {
        Team team = getTeam(teamId);

        if (!team.isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 팀 상태를 변경할 수 있습니다.");
        }

        team.changeStatus(status);
    }

    public void leaveTeam(Long userId) {
        User user = userService.getUser(userId);
        Team team = getTeam(user.getTeamId());

        if (team.isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더는 팀을 탈퇴할 수 없습니다.");
        }

        team.removeMember(user);
    }

    public void deleteTeam(Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("가입된 팀이 없습니다.");
        }

        Team team = getTeam(user.getTeamId());

        if (!team.isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 팀을 삭제할 수 있습니다.");
        }

        finishMatchTransaction(team);
        List<User> users = userService.getMembers(user.getTeamId());
        users.forEach(team::removeMember);
        teamRepository.deleteById(team.getId());
    }

    public void finishMatch(Long userId) {
        User user = userService.getUser(userId);

        if (!user.hasTeam()) {
            throw new ForbiddenBehaviorException("가입된 팀이 없습니다.");
        }

        Team team = getTeam(user.getTeamId());

        if (!team.isLedBy(userId)) {
            throw new ForbiddenBehaviorException("리더만 매칭을 끝낼 수 있습니다.");
        }

        finishMatchTransaction(team);
    }

    private void finishMatchTransaction(Team team) {
        Team matchedTeam = getTeam(team.getMatchedTeamId());
        team.finishMatch();
        matchedTeam.finishMatch();
    }
}
