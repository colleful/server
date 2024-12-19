package com.colleful.server.invitation.repository;

import com.colleful.server.invitation.domain.Invitation;
import com.colleful.server.team.domain.Team;
import com.colleful.server.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findAllByUser(User user);

    List<Invitation> findAllByTeam(Team team);

    boolean existsByTeamAndUser(Team team, User user);

    void deleteAllByUser(User user);
}
