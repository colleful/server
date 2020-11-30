package com.colleful.server.domain.matchrequest;

import lombok.Getter;

public class MatchDto {

    @Getter
    public static class Response {

        private final Long id;
        private final Long teamId;
        private final String teamName;

        public Response(MatchRequest match) {
            this.id = match.getId();
            this.teamId = match.getSender().getId();
            this.teamName = match.getSender().getTeamName();
        }
    }
}
