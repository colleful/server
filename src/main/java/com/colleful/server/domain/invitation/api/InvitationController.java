package com.colleful.server.domain.invitation.api;

import com.colleful.server.domain.invitation.dto.InvitationDto.Response;
import com.colleful.server.domain.invitation.service.InvitationService;
import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.global.security.JwtProvider;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/api/invitation")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;
    private final JwtProvider provider;

    @GetMapping
    public List<Response> getAllInvitations(@RequestHeader("Access-Token") String token) {
        List<Invitation> invitations = invitationService.getAllInvitations(provider.getId(token));
        List<Response> responses = new ArrayList<>();
        for (Invitation invitation : invitations) {
            responses.add(new Response(invitation));
        }
        return responses;
    }

    @PostMapping("/{team-id}/{target-user-id}")
    public ResponseEntity<?> invite(@RequestHeader(value = "Access-Token") String token,
        @PathVariable("team-id") Long teamId, @PathVariable("target-user-id") Long targetId) {
        Long invitationId = invitationService.invite(teamId, targetId, provider.getId(token));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/invitation/" + invitationId);
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        invitationService.accept(id, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@RequestHeader("Access-Token") String token,
        @PathVariable Long id) {
        invitationService.refuse(id, provider.getId(token));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
