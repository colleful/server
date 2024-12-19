package com.colleful.server.matching.api;

import com.colleful.server.global.security.JwtProperties;
import com.colleful.server.matching.dto.MatchingRequestDto;
import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.matching.service.MatchingRequestService;
import com.colleful.server.global.security.JwtProvider;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<?> request(@RequestHeader(JwtProperties.HEADER) String token,
        @RequestBody MatchingRequestDto.Request dto) {
        MatchingRequest match = matchingRequestService
            .request(provider.getId(token), dto.getTeamId());
        return ResponseEntity.created(URI.create("/api/matching/" + match.getId()))
            .body(new MatchingRequestDto.Response(match));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        matchingRequestService.accept(provider.getId(token), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        matchingRequestService.refuse(provider.getId(token), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        matchingRequestService.cancel(provider.getId(token), id);
        return ResponseEntity.ok().build();
    }
}
