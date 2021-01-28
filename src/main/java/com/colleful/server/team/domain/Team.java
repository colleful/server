package com.colleful.server.team.domain;

import com.colleful.server.user.domain.Gender;
import com.colleful.server.user.domain.User;
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
    private Integer headcount;

    @Column(nullable = false)
    private Long leaderId;

    @Column
    private Long matchedTeamId;

    public boolean isLedBy(Long userId) {
        return this.leaderId.equals(userId);
    }

    public boolean isDifferentGenderFrom(Gender gender) {
        return this.gender != gender;
    }

    public boolean isNotReady() {
        return this.status != TeamStatus.READY;
    }

    public boolean isMatched() {
        return this.matchedTeamId != null;
    }

    public void addMember(User user) {
        user.joinTeam(this.id);
        this.headcount++;
    }

    public void removeMember(User user) {
        if (user.isNotMemberOf(this.id)) {
            throw new ForbiddenBehaviorException("이 팀의 멤버가 아닙니다.");
        }

        user.leaveTeam();
        this.headcount--;
    }

    public void changeStatus(TeamStatus status) {
        this.status = status;
    }

    public void match(Long teamId) {
        if (this.isMatched()) {
            throw new ForbiddenBehaviorException("이미 다른 팀과 매칭되어 있습니다.");
        }

        this.matchedTeamId = teamId;
        this.status = TeamStatus.MATCHED;
    }

    public void finishMatch(Team matchedTeam) {
        if (!this.matchedTeamId.equals(matchedTeam.getId())) {
            throw new ForbiddenBehaviorException("매칭된 팀이 아닙니다.");
        }

        this.matchedTeamId = null;
        this.status = TeamStatus.PENDING;
        matchedTeam.matchedTeamId = null;
        matchedTeam.status = TeamStatus.PENDING;
    }
}
