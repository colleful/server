package com.colleful.server.invitation.dto;

import com.colleful.server.invitation.domain.Invitation;
import com.colleful.server.team.dto.TeamDto;
import java.time.LocalDateTime;
import lombok.Getter;

public class InvitationDto {

    @Getter
    public static class Request {

        private Long userId;
    }

    @Getter
    public static class Response {

        private final Long id;
        private final LocalDateTime createdAt;
        private final TeamDto.Response team;

        public Response(Invitation invitation) {
            this.id = invitation.getId();
            this.createdAt = invitation.getCreatedAt();
            this.team = new TeamDto.Response(invitation.getTeam());
        }
    }
}
