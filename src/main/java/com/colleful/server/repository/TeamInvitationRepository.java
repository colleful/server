package com.colleful.server.repository;

import com.colleful.server.domain.Team;
import com.colleful.server.domain.TeamInvitation;
import com.colleful.server.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    List<TeamInvitation> findAllByUser(User user);
    boolean existsByTeamAndUser(Team team, User user);
}
