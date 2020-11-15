package com.ocupid.server.service;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.TeamStatus;
import com.ocupid.server.domain.User;
import com.ocupid.server.repository.TeamRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberService teamMemberService;

    public TeamService(TeamRepository teamRepository,
        TeamMemberService teamMemberService) {
        this.teamRepository = teamRepository;
        this.teamMemberService = teamMemberService;
    }

    public Boolean createTeam(Team team) {
        try {
            teamRepository.save(team);
            TeamMember member = new TeamMember(team, team.getLeader());
            return teamMemberService.addMember(member);
        } catch (Exception e) {
            return false;
        }
    }

    public Page<Team> getAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    public Optional<Team> getTeamInfo(Long id) {
        return teamRepository.findById(id);
    }

    public Page<Team> getAllReadyTeams(Pageable pageable) {
        return teamRepository.findAllByStatusOrderByUpdatedAtDesc(pageable, TeamStatus.READY);
    }

    public Page<Team> searchTeams(Pageable pageable, String teamName) {
        return teamRepository
            .findAllByStatusAndTeamNameContainingOrderByUpdatedAtDesc(pageable,
                TeamStatus.READY, teamName);
    }

    public List<Team> getAllTeamsByLeader(User leader) {
        return teamRepository.findAllByLeader(leader);
    }

    public Boolean changeTeamInfo(Team team, String teamName) {
        try {
            team.setTeamName(teamName);
            teamRepository.save(team);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateTeamStatus(Team team, TeamStatus status) {
        try {
            team.setStatus(status);
            teamRepository.save(team);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteTeam(Team team) {
        try {
            if (!clearMatch(team)) {
                return false;
            }
            teamRepository.deleteById(team.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean clearMatch(Team team) {
        try {
            if (team.getMatchedTeam() != null) {
                team.getMatchedTeam().setMatchedTeam(null);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean saveMatchInfo(Team teamSend,Team teamReceive){
        try {
            teamSend.setMatchedTeam(teamReceive);
            teamSend.setStatus(TeamStatus.MATCHED);
            teamReceive.setMatchedTeam(teamSend);
            teamReceive.setStatus(TeamStatus.MATCHED);
            teamRepository.save(teamSend);
            teamRepository.save(teamReceive);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
