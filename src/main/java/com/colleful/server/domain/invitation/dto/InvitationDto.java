package com.colleful.server.domain.invitation.dto;

import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.team.dto.TeamDto.Response;
import lombok.Getter;

public class InvitationDto {

    @Getter
    public static class Response {

        private final Long id;
        private final TeamDto.Response team;

        public Response(Invitation invitation) {
            this.id = invitation.getId();
            this.team = new TeamDto.Response(invitation.getTeam());
        }
    }
}
