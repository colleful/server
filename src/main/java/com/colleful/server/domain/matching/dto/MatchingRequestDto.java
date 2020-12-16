package com.colleful.server.domain.matching.dto;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import lombok.Getter;

public class MatchingRequestDto {

    @Getter
    public static class Response {

        private final Long id;
        private final Long teamId;
        private final String teamName;

        public Response(MatchingRequest match) {
            this.id = match.getId();
            this.teamId = match.getSender().getId();
            this.teamName = match.getSender().getTeamName();
        }
    }
}
