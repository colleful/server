package com.ocupid.server.domain;

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
public class TeamMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team teamSend;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Team teamReceive;

    public TeamMatch() {
    }

    public TeamMatch(Team teamSend,Team teamReceive) {
        this.teamSend = teamSend;
        this.teamReceive = teamReceive;
    }
}
