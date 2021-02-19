package com.colleful.server.matching.domain;

import com.colleful.server.global.exception.ForbiddenBehaviorException;
import com.colleful.server.team.domain.Team;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MatchingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team sentTeam;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team receivedTeam;

    public MatchingRequest(Team sentTeam, Team receivedTeam) {
        if (sentTeam.hasSameGenderWith(receivedTeam.getGender())) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (receivedTeam.isNotReady()) {
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        this.sentTeam = sentTeam;
        this.receivedTeam = receivedTeam;
    }

    public boolean isNotSentBy(Long userId) {
        return !this.sentTeam.isLedBy(userId);
    }

    public boolean isNotReceivedBy(Long userId) {
        return !this.receivedTeam.isLedBy(userId);
    }

    public void accept() {
        this.sentTeam.match(this.receivedTeam.getId());
        this.receivedTeam.match(this.sentTeam.getId());
    }
}
