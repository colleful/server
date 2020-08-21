package com.ocupid.server.dto;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMember;
import com.ocupid.server.domain.User;
import com.ocupid.server.service.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class TeamDto {

    @Getter
    private static class SimplifiedUser {

        private final Long id;
        private final String gender;
        private final String collage;

        public SimplifiedUser(User user) {
            this.id = user.getId();
            this.gender = user.getGender();
            this.collage = user.getCollage();
        }
    }

    @Getter
    public static class Request {

        private String teamName;
        private Integer headcount;
        private String gender;
        private String collage;
        private String status;
        private Long leaderId;

        public Team toEntity(UserService userService) {
            User leader = userService.getUserInfo(leaderId).orElseThrow(RuntimeException::new);
            Team team = new Team();
            team.setTeamName(teamName);
            team.setHeadcount(headcount);
            team.setGender(gender == null ? leader.getGender() : gender);
            team.setCollage(collage == null ? leader.getCollage() : collage);
            team.setStatus(
                status == null ?
                    headcount == 1 ? "ready" : "pending" :
                    status
            );
            team.setLeader(leader);
            return team;
        }
    }

    @Getter
    public static class Response {

        private final String teamName;
        private final Integer headcount;
        private final String gender;
        private final String collage;
        private final String status;
        private final SimplifiedUser leader;
        private final List<SimplifiedUser> members;

        public Response(Team team) {
            this.teamName = team.getTeamName();
            this.headcount = team.getHeadcount();
            this.gender = team.getGender();
            this.collage = team.getCollage();
            this.status = team.getStatus();
            this.leader = new SimplifiedUser(team.getLeader());
            this.members = new ArrayList<>();
            for (TeamMember member : team.getMembers()) {
                members.add(new SimplifiedUser(member.getMember()));
            }
        }
    }
}
