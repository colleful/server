package com.colleful.server.domain.team;

import com.colleful.server.domain.constant.TeamStatus;
import java.time.LocalDateTime;
import lombok.Getter;

public class TeamDto {

    @Getter
    public static class Request {

        private String teamName;
        private String status;

        public Team toEntity(Long leaderId) {
            Team team = new Team();
            team.setTeamName(teamName);
            team.setStatus(status == null ? TeamStatus.PENDING : TeamStatus.valueOf(status));
            team.setLeaderId(leaderId);
            return team;
        }
    }

    @Getter
    public static class Response {

        private final Long id;
        private final LocalDateTime updatedAt;
        private final String teamName;
        private final String gender;
        private final String status;
        private final Long leaderId;

        public Response(Team team) {
            this.id = team.getId();
            this.updatedAt = team.getUpdatedAt();
            this.teamName = team.getTeamName();
            this.gender = team.getGender().name();
            this.status = team.getStatus().name();
            this.leaderId = team.getLeaderId();
        }
    }
}
