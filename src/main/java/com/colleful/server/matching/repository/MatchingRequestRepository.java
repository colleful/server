package com.colleful.server.matching.repository;

import com.colleful.server.matching.domain.MatchingRequest;
import com.colleful.server.team.domain.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, Long> {

    List<MatchingRequest> findAllByReceivedTeam(Team receivedTeam);

    List<MatchingRequest> findAllBySentTeam(Team sentTeam);

    boolean existsBySentTeamAndReceivedTeam(Team sentTeam, Team receivedTeam);

    void deleteAllByReceivedTeam(Team receivedTeam);
}
