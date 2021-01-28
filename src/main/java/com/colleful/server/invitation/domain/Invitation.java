package com.colleful.server.invitation.domain;

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
            throw new ForbiddenBehaviorException("이미 팀에 가입된 유저입니다.");
        }

        if (team.isDifferentGenderFrom(user.getGender())) {
            throw new ForbiddenBehaviorException("같은 성별만 초대할 수 있습니다.");
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
