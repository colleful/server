package com.ocupid.server.service;

import com.ocupid.server.domain.TeamMember;
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
            teamMemberRepository.save(teamMember);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
