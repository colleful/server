package com.colleful.server.domain.matching.dto;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.team.dto.TeamDto;
import lombok.Getter;

public class MatchingRequestDto {

    @Getter
    public static class Response {

        private final Long id;
        private final TeamDto.Response sender;

        public Response(MatchingRequest match) {
            this.id = match.getId();
            this.sender = new TeamDto.Response(match.getSender());
        }
    }
}
