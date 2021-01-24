package com.colleful.server.matching.repository;

import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.team.domain.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {

    List<MatchingRequest> findAllByReceiver(Team receiver);

    List<MatchingRequest> findAllBySender(Team sender);

    boolean existsBySenderAndReceiver(Team sender, Team receiver);

    void deleteAllByReceiver(Team receiver);
}
