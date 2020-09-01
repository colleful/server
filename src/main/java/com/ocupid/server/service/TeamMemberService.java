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

            if (!member.getDepartment().getCollege()
                .equals(team.getLeader().getDepartment().getCollege())) {
                return false;
            }

            teamMemberRepository.save(teamMember);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
