package com.colleful.server.domain.teammatchrequest;

import com.colleful.server.domain.team.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMatchRequestRepository extends JpaRepository<TeamMatchRequest, Long>{
    List<TeamMatchRequest> findAllByReceiver_LeaderId(Long leaderId);
    boolean existsBySenderAndReceiver(Team sender, Team receiver);
}
