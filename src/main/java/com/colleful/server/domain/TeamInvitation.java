package com.colleful.server.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    public TeamInvitation() {}

    public TeamInvitation(Team team, User user) {
        this.team = team;
        this.user = user;
    }

    public boolean isNotForMe(Long userId) {
        return !this.user.getId().equals(userId);
    }
}