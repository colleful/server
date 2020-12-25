package com.colleful.server.domain.team.domain;

import com.colleful.server.domain.user.domain.Gender;
import com.colleful.server.global.exception.ForbiddenBehaviorException;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String teamName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
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

    public void changeStatus(TeamStatus status) {
        this.status = status;
    }

    public void match(Long matchedTeamId) {
        if (this.matchedTeamId != null) {
            throw new ForbiddenBehaviorException("이미 다른 팀과 매칭되어 있습니다.");
        }
        this.matchedTeamId = matchedTeamId;
        this.status = TeamStatus.MATCHED;
    }

    public void endMatch() {
        this.matchedTeamId = null;
    }
}
