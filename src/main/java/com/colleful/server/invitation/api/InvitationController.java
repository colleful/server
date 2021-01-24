package com.colleful.server.invitation.api;

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
    public List<InvitationDto.Response> getAllInvitationsFromMyTeam(
        @RequestHeader("Authorization") String token) {
        List<Invitation> invitations = invitationService
            .getAllInvitationsFromMyTeam(provider.getId(token));
        return invitations.stream().map(InvitationDto.Response::new).collect(Collectors.toList());
    }

    @GetMapping("/received")
    public List<InvitationDto.Response> getAllInvitationsToMe(
        @RequestHeader("Authorization") String token) {
        List<Invitation> invitations = invitationService
            .getAllInvitationsToMe(provider.getId(token));
        return invitations.stream().map(InvitationDto.Response::new).collect(Collectors.toList());
    }

    @PostMapping("/{user-id}")
    public ResponseEntity<?> invite(@RequestHeader(value = "Authorization") String token,
        @PathVariable("user-id") Long userId) {
        Long invitationId = invitationService.invite(userId, provider.getId(token));
        return ResponseEntity.created(URI.create("/api/invitation" + invitationId)).build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@RequestHeader("Authorization") String token,
        @PathVariable Long id) {
        invitationService.accept(id, provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@RequestHeader("Authorization") String token,
        @PathVariable Long id) {
        invitationService.refuse(id, provider.getId(token));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@RequestHeader("Authorization") String token,
        @PathVariable Long id) {
        invitationService.cancel(id, provider.getId(token));
        return ResponseEntity.ok().build();
    }
}
