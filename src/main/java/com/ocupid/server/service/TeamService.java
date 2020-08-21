package com.ocupid.server.service;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.repository.TeamRepository;
import java.util.List;
import java.util.Optional;
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
            TeamMember member = new TeamMember();
            member.setTeam(team);
            member.setMember(team.getLeader());
            return teamMemberService.addMember(member);
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Team> getTeamInfo(Long id) {
        return teamRepository.findById(id);
    }

    public List<Team> getAllReadyTeams() {
        return teamRepository.getAllByStatus("ready");
    }
}
