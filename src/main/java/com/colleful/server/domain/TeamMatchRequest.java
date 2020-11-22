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
public class TeamMatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team sender;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team receiver;

    public TeamMatchRequest() {}

    public TeamMatchRequest(Team sender, Team receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
