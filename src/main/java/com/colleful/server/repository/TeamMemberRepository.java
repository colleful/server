package com.colleful.server.repository;

import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamMember;
import com.colleful.server.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamAndMember(Team team, User member);
    Optional<TeamMember> findByMember(User member);
    boolean existsByTeamAndMember(Team team, User member);
}
