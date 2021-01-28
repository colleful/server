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
    private Team sender;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team receiver;

    public MatchingRequest(Team sender, Team receiver) {
        if (!sender.isDifferentGenderFrom(receiver.getGender())) {
            throw new ForbiddenBehaviorException("다른 성별에게만 매칭 요청할 수 있습니다.");
        }

        if (receiver.isNotReady()) {
            throw new ForbiddenBehaviorException("준비된 팀에게만 매칭 요청할 수 있습니다.");
        }

        this.sender = sender;
        this.receiver = receiver;
    }

    public boolean isNotSentBy(Long userId) {
        return !this.sender.isLedBy(userId);
    }

    public boolean isNotReceivedBy(Long userId) {
        return !this.receiver.isLedBy(userId);
    }

    public void accept() {
        this.sender.match(this.receiver.getId());
        this.receiver.match(this.sender.getId());
    }
}
