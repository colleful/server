package com.colleful.server.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@JsonIdentityInfo(generator = IntSequenceGenerator.class, property = "id")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String teamName;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private TeamStatus status;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User leader;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> members = new ArrayList<>();

    @OneToOne
    @JoinColumn(unique = true)
    private Team matchedTeam;

    public boolean isNotLeader(Long userId) {
        return !this.leader.getId().equals(userId);
    }

    public boolean isDifferentGender(Gender gender) {
        return this.gender.compareTo(gender) != 0;
    }

    public boolean isNotReady() {
        return this.status.compareTo(TeamStatus.READY) != 0;
    }
}
