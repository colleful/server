package com.colleful.server.domain.team;

import com.colleful.server.domain.user.Gender;
import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
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

    @Column(nullable = false)
    private Long leaderId;

    @Column
    private Long matchedTeamId;

    public boolean isNotLeader(Long userId) {
        return !this.leaderId.equals(userId);
    }

    public boolean isDifferentGender(Gender gender) {
        return this.gender.compareTo(gender) != 0;
    }

    public boolean isNotReady() {
        return this.status.compareTo(TeamStatus.READY) != 0;
    }
}
