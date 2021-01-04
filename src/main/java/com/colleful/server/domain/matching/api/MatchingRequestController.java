package com.colleful.server.domain.matching.api;

import com.colleful.server.domain.matching.dto.MatchingRequestDto;
import com.colleful.server.domain.matching.domain.MatchingRequest;
import com.colleful.server.domain.matching.service.MatchingRequestService;
import com.colleful.server.global.security.JwtProvider;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public List<MatchingRequestDto.Response> getAllMatchRequests(
        @RequestHeader("Access-Token") String token) {
        List<MatchingRequest> matches = matchingRequestService
            .getAllMatchRequests(provider.getId(token));
        return matches.stream().map(MatchingRequestDto.Response::new).collect(Collectors.toList());
    }

    @PostMapping("/{sender-id}/{receiver-id}")
    public ResponseEntity<?> request(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("sender-id") Long senderId,
        @PathVariable("receiver-id") Long receiverId) {
        Long requestId = matchingRequestService
            .request(senderId, receiverId, provider.getId(token));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/matching/" + requestId);
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        matchingRequestService.accept(id, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        matchingRequestService.refuse(id, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
