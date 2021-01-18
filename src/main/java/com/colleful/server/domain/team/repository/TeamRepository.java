package com.colleful.server.domain.team.repository;

import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Page<Team> findAll(Pageable pageable);

    Page<Team> findAllByStatusOrderByUpdatedAtDesc(Pageable pageable, TeamStatus status);

    Page<Team> findAllByStatusAndTeamNameContainingOrderByUpdatedAtDesc(Pageable pageable,
        TeamStatus status, String teamName);
}
