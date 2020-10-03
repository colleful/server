package com.ocupid.server.service;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.User;
import com.ocupid.server.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public Boolean addMember(TeamMember teamMember) {
        try {
            Team team = teamMember.getTeam();
            User member = teamMember.getMember();

            if (!member.getGender().equals(team.getGender())) {
                return false;
            }

            teamMemberRepository.save(teamMember);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean leaveTeam(Team team, User member) {
        try {
            TeamMember result = teamMemberRepository
                .findByTeamAndMember(team, member).orElseThrow();
            teamMemberRepository.deleteById(result.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean alreadyJoined(Team team, User member) {
        return teamMemberRepository.existsByTeamAndMember(team, member);
    }
}
