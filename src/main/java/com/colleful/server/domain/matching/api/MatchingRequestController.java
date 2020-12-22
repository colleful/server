package com.colleful.server.domain.matching.api;

import com.colleful.server.domain.matching.dto.MatchingRequestDto.Response;
import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.service.MatchingRequestService;
import com.colleful.server.global.security.JwtProvider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MatchingRequestController {

    private final MatchingRequestService matchingRequestService;
    private final JwtProvider provider;

    @GetMapping
    public List<Response> getAllMatchRequests(@RequestHeader("Access-Token") String token) {
        List<MatchingRequest> matches = matchingRequestService
            .getAllMatchRequests(provider.getId(token));
        List<Response> responses = new ArrayList<>();
        for (MatchingRequest match : matches) {
            responses.add(new Response(match));
        }
        return responses;
    }

    @PostMapping("/{sender-id}/{receiver-id}")
    public ResponseEntity<?> createMatchRequest(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("sender-id") Long senderId,
        @PathVariable("receiver-id") Long receiverId) {
        matchingRequestService.sendMatchRequest(senderId, receiverId, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptMatchRequest(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        matchingRequestService.accept(id, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuseMatchRequest(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        matchingRequestService.refuse(id, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
