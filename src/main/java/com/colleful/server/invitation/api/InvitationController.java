package com.colleful.server.invitation.api;

import com.colleful.server.global.security.JwtProperties;
import com.colleful.server.invitation.dto.InvitationDto;
import com.colleful.server.invitation.service.InvitationService;
import com.colleful.server.invitation.domain.Invitation;
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
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;
    private final JwtProvider provider;

    @GetMapping("/sent")
    public List<InvitationDto.Response> getAllSentInvitations(
        @RequestHeader(JwtProperties.HEADER) String token) {
        List<Invitation> invitations = invitationService
            .getAllSentInvitations(provider.getId(token));
        return invitations.stream().map(InvitationDto.Response::new).collect(Collectors.toList());
    }

    @GetMapping("/received")
    public List<InvitationDto.Response> getAllReceivedInvitations(
        @RequestHeader(JwtProperties.HEADER) String token) {
        List<Invitation> invitations = invitationService
            .getAllReceivedInvitations(provider.getId(token));
        return invitations.stream().map(InvitationDto.Response::new).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> invite(@RequestHeader(JwtProperties.HEADER) String token,
        @RequestBody InvitationDto.Request dto) {
        Invitation invitation = invitationService.invite(provider.getId(token), dto.getUserId());
        return ResponseEntity.created(URI.create("/api/invitations/" + invitation.getId()))
            .body(new InvitationDto.Response(invitation));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        invitationService.accept(provider.getId(token), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        invitationService.refuse(provider.getId(token), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@RequestHeader(JwtProperties.HEADER) String token,
        @PathVariable Long id) {
        invitationService.cancel(provider.getId(token), id);
        return ResponseEntity.ok().build();
    }
}
