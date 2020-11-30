package com.colleful.server.domain.teammember;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamAndMember(Team team, User member);
    List<TeamMember> findByTeam(Team team);
    List<TeamMember> findByMember(User member);
    boolean existsByTeamAndMember(Team team, User member);
}
