package com.ocupid.server.service;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMatch;
import com.ocupid.server.repository.TeamMatchRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TeamMatchService {

    TeamMatchRepository teamMatchRepository;

    public TeamMatchService(
            TeamMatchRepository teamMatchRepository) {
        this.teamMatchRepository = teamMatchRepository;
    }

    public Boolean sendMatchRequest(TeamMatch match) {
        try {
            teamMatchRepository.save(match);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<TeamMatch> getMatchRequest(Long id) {
        return teamMatchRepository.findById(id);
    }

    public List<TeamMatch> getAllMatchRequests(Team team) {
        return teamMatchRepository.findAllByTeamReceive(team);
    }

    public boolean alreadyRequestedMatch(Team teamSend, Team teamReceived) {
        return teamMatchRepository.existsByTeamSendAndTeamReceive(teamSend, teamReceived);
    }

    public Boolean endMatch(Long id) {
        try {
            teamMatchRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
