package com.colleful.server.domain.teammember;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import java.util.List;
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

    public List<TeamMember> getMemberInfoByTeam(Team team) {
        return teamMemberRepository.findByTeam(team);
    }

    public List<TeamMember> getTeamInfoByUser (User member){
        return teamMemberRepository.findByMember(member);
    }
}
