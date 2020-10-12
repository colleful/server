package com.ocupid.server.dto;

import com.ocupid.server.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lombok.Getter;

public class TeamDto {

    @Getter
    private static class SimplifiedUser {

        private final Long id;
        private final String nickname;
        private final Integer age;
        private final String gender;
        private final String department;

        public SimplifiedUser(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.age = Calendar.getInstance().get(Calendar.YEAR) - user.getBirthYear() + 1;
            this.gender = user.getGender().name();
            this.department = user.getDepartment().getDepartmentName();
        }
    }

    @Getter
    public static class Request {

        private String teamName;
        private String status;

        public Team toEntity(User leader) {
            Team team = new Team();
            team.setTeamName(teamName);
            team.setGender(leader.getGender());
            team.setStatus(status == null ? TeamStatus.PENDING : TeamStatus.valueOf(status));
            team.setLeader(leader);
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
        private final List<SimplifiedUser> members;

        public Response(Team team) {
            Integer sumOfAge = 0;
            this.id = team.getId();
            this.updatedAt = team.getUpdatedAt();
            this.teamName = team.getTeamName();
            this.headcount = team.getMembers().size();
            this.gender = team.getGender().name();
            this.status = team.getStatus().name();
            this.leaderId = team.getLeader().getId();
            this.members = new ArrayList<>();
            for (TeamMember member : team.getMembers()) {
                SimplifiedUser simplifiedUser = new SimplifiedUser(member.getMember());
                sumOfAge += simplifiedUser.getAge();
                members.add(simplifiedUser);
            }
            this.averageAge = sumOfAge.doubleValue() / team.getMembers().size();
        }
    }

    @Getter
    public static class MatchResponse {

        private final Long id;
        private final TeamDto.Response teamSend;

        public MatchResponse(TeamMatch match) {
            this.id = match.getId();
            this.teamSend = new TeamDto.Response(match.getTeamSend());
        }
    }
}
