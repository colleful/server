package com.colleful.server.domain.team.dto;

import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.Getter;

public class TeamDto {

    @Getter
    public static class Request {

        private String teamName;
        private String status;

        public Team toEntity(User leader) {
            return Team.builder()
                .teamName(teamName)
                .status(status == null ? TeamStatus.PENDING : TeamStatus.valueOf(status))
                .leaderId(leader.getId())
                .gender(leader.getGender())
                .build();
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
