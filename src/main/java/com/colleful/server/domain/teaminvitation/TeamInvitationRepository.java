package com.colleful.server.domain.teaminvitation;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    List<TeamInvitation> findAllByUser(User user);
    boolean existsByTeamAndUser(Team team, User user);
}
