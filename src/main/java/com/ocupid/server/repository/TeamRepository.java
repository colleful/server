package com.ocupid.server.repository;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamStatus;
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
