package com.colleful.server.team.domain;

import com.colleful.server.global.exception.ErrorType;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
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

    private Long matchedTeamId;

    public static Team of(String teamName, User leader) {
        Team team = Team.builder()
            .teamName(teamName)
            .gender(leader.getGender())
            .status(TeamStatus.PENDING)
            .headcount(0)
            .leaderId(leader.getId())
            .build();

        team.addMember(leader);

        return team;
    }

    public static Team getEmptyInstance() {
        return new Team();
    }

    public boolean isNotEmpty() {
        return this.id != null
            || this.updatedAt != null
            || this.teamName != null
            || this.gender != null
            || this.status != null
            || this.headcount != null
            || this.leaderId != null
            || this.matchedTeamId != null;
    }

    public boolean isLedBy(Long userId) {
        return this.leaderId.equals(userId);
    }

    public boolean isNotLedBy(Long userId) {
        return !this.isLedBy(userId);
    }

    public boolean hasSameGenderWith(Gender gender) {
        return this.gender == gender;
    }

    public boolean hasDifferentGenderFrom(Gender gender) {
        return !this.hasSameGenderWith(gender);
    }

    public boolean isNotReady() {
        return this.status != TeamStatus.READY;
    }

    public boolean isNotPending() {
        return this.status != TeamStatus.PENDING;
    }

    public boolean isMatched() {
        return this.matchedTeamId != null;
    }

    public boolean isNotMatched() {
        return !this.isMatched();
    }

    public boolean isNotAccessibleTo(User user) {
        return this.isNotEmpty() && this.isNotReady() && user.isNotMemberOf(this.id);
    }

    public boolean isNotMatchableWith(Team team) {
        return this.hasSameGenderWith(team.getGender())
            || team.isNotReady();
    }

    public boolean cannotInvite(User user) {
        return this.hasDifferentGenderFrom(user.getGender())
            || this.isNotPending();
    }

    public void addMember(User user) {
        this.headcount++;
        user.joinTeam(this.id);
    }

    public void removeMember(User user) {
        if (user.isNotMemberOf(this.id)) {
            throw new ForbiddenBehaviorException(ErrorType.IS_NOT_MEMBER);
        }

        user.leaveTeam();
        this.headcount--;
    }

    public void changeStatus(TeamStatus status) {
        this.status = status;
    }

    public void match(Long teamId) {
        if (this.isMatched()) {
            throw new ForbiddenBehaviorException(ErrorType.ALREADY_MATCHED);
        }

        this.matchedTeamId = teamId;
        this.status = TeamStatus.MATCHED;
    }

    public void finishMatch() {
        this.matchedTeamId = null;
        this.status = TeamStatus.PENDING;
    }
}
