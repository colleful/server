package com.colleful.server.domain.matchrequest;

import com.colleful.server.domain.team.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long>{
    List<MatchRequest> findAllByReceiver_LeaderId(Long leaderId);
    boolean existsBySenderAndReceiver(Team sender, Team receiver);
}
