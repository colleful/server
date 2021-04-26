package com.colleful.server.invitation.domain;

import com.colleful.server.global.exception.ErrorType;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.team.domain.Team;
import com.colleful.server.user.domain.User;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public Invitation(Team team, User user) {
        if (user.hasTeam()) {
            throw new ForbiddenBehaviorException(ErrorType.ALREADY_HAS_TEAM);
        }

        if (team.cannotInvite(user)) {
            throw new ForbiddenBehaviorException(ErrorType.CANNOT_INVITE);
        }

        this.team = team;
        this.user = user;
    }

    public boolean isNotReceivedBy(Long userId) {
        return !this.user.getId().equals(userId);
    }

    public boolean isNotSentBy(Long userId) {
        return !this.team.isLedBy(userId);
    }

    public void accept() {
        this.team.addMember(this.user);
    }
}
