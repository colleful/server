package com.colleful.server.domain.team;

import com.colleful.server.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Boolean createTeam(Team team) {
        try {
            teamRepository.save(team);
            return true;
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

    public List<Team> getAllTeamsByLeader(Long leaderId) {
        return teamRepository.findAllByLeaderId(leaderId);
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

    @Transactional
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
            if (team.getMatchedTeamId() != null) {
                team.setMatchedTeamId(null);
                teamRepository.save(team);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Boolean saveMatchInfo(Team sender, Team receiver){
        try {
            sender.setMatchedTeamId(receiver.getId());
            sender.setStatus(TeamStatus.MATCHED);
            receiver.setMatchedTeamId(sender.getId());
            receiver.setStatus(TeamStatus.MATCHED);
            teamRepository.save(sender);
            teamRepository.save(receiver);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
