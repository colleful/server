package com.colleful.server.matching.api;

import com.colleful.server.global.security.JwtProperties;
import com.colleful.server.matching.dto.MatchingRequestDto;
import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.matching.service.MatchingRequestService;
import com.colleful.server.global.security.JwtProvider;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/sent")
    public List<MatchingRequestDto.Response> getAllSentMatchingRequest(
        @RequestHeader(JwtProperties.HEADER) String token) {
        List<MatchingRequest> matches = matchingRequestService
            .getAllSentMatchingRequests(provider.getId(token));
        return matches.stream().map(MatchingRequestDto.Response::new).collect(Collectors.toList());
    }

    @GetMapping("/received")
    public List<MatchingRequestDto.Response> getAllReceivedMatchingRequests(
        @RequestHeader(JwtProperties.HEADER) String token) {
        List<MatchingRequest> matches = matchingRequestService
            .getAllReceivedMatchingRequests(provider.getId(token));
        return matches.stream().map(MatchingRequestDto.Response::new).collect(Collectors.toList());
    }

    @PostMapping("/{team-id}")
    public ResponseEntity<?> request(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable("team-id") Long teamId) {
        Long requestId = matchingRequestService
            .request(teamId, provider.getId(token));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/matching/" + requestId);
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        matchingRequestService.accept(id, provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        matchingRequestService.refuse(id, provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        matchingRequestService.cancel(id, provider.getId(token));
        return ResponseEntity.ok().build();
    }
}
