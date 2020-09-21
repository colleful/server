package com.ocupid.server.repository;

import com.ocupid.server.domain.Team;
import com.ocupid.server.domain.TeamInvitation;
import com.ocupid.server.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    List<TeamInvitation> findAllByUser(User user);
    boolean existsByTeamAndUser(Team team, User user);
}
