package com.ocupid.server.repository;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamMatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMatchRepository extends JpaRepository<TeamMatch, Long>{
    List<TeamMatch> findAllByTeamReceive(Team team);
    boolean existsByTeamSendAndTeamReceive(Team teamSend, Team teamReceive);
}
