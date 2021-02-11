package com.colleful.server.matching.dto;

import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.team.dto.TeamDto;
import java.time.LocalDateTime;
import lombok.Getter;

public class MatchingRequestDto {

    @Getter
    public static class Request {

        private Long teamId;
    }

    @Getter
    public static class Response {

        private final Long id;
        private final LocalDateTime createdAt;
        private final TeamDto.Response sender;

        public Response(MatchingRequest match) {
            this.id = match.getId();
            this.createdAt = match.getCreatedAt();
            this.sender = new TeamDto.Response(match.getSender());
        }
    }
}
