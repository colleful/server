package com.colleful.server.dto;

import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamMatchRequest;
import com.colleful.server.domain.TeamMember;
import com.colleful.server.domain.TeamStatus;
import com.colleful.server.domain.User;
import com.colleful.server.dto.UserDto.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Getter;

public class TeamDto {

    @Getter
    public static class Request {

        private String teamName;
        private String status;

        public Team toEntity(User leader) {
            Team team = new Team();
            team.setTeamName(teamName);
            team.setGender(leader.getGender());
            team.setStatus(status == null ? TeamStatus.PENDING : TeamStatus.valueOf(status));
            team.setLeaderId(leader.getId());
            return team;
        }
    }

    @Getter
    public static class Response {

        private final Long id;
        private final LocalDateTime updatedAt;
        private final String teamName;
        private final Integer headcount;
        private final String gender;
        private final String status;
        private final Long leaderId;
        private final Double averageAge;
        private final List<UserDto.Response> members;

        public Response(Team team, List<TeamMember> members) {
            Integer sumOfAge = 0;
            this.id = team.getId();
            this.updatedAt = team.getUpdatedAt();
            this.teamName = team.getTeamName();
            this.headcount = members.size();
            this.gender = team.getGender().name();
            this.status = team.getStatus().name();
            this.leaderId = team.getLeaderId();
            this.members = new ArrayList<>();
            for (TeamMember member : members) {
                UserDto.Response userDto = new UserDto.Response(member.getMember());
                sumOfAge += userDto.getAge();
                this.members.add(userDto);
            }
            this.averageAge = sumOfAge.doubleValue() / members.size();
        }
    }

    @Getter
    public static class MatchResponse {

        private final Long id;
        private final Long teamId;
        private final String teamName;

        public MatchResponse(TeamMatchRequest match) {
            this.id = match.getId();
            this.teamId = match.getSender().getId();
            this.teamName = match.getSender().getTeamName();
        }
    }
}
