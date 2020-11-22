package com.colleful.server.repository;

import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamMatchRequest;
import com.colleful.server.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMatchRequestRepository extends JpaRepository<TeamMatchRequest, Long>{
    List<TeamMatchRequest> findAllByReceiver_Leader(User leader);
    boolean existsBySenderAndReceiver(Team sender, Team receiver);
}
