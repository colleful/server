package com.colleful.server.domain.invitation.dto;

import com.colleful.server.domain.invitation.domain.Invitation;
import lombok.Getter;

public class InvitationDto {

    @Getter
    public static class Response {

        private final Long id;
        private final Long teamId;
        private final String teamName;

        public Response(Invitation invitation) {
            this.id = invitation.getId();
            this.teamId = invitation.getTeam().getId();
            this.teamName = invitation.getTeam().getTeamName();
        }
    }
}
