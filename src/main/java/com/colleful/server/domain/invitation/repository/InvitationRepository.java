package com.colleful.server.domain.invitation.repository;

import com.colleful.server.domain.invitation.domain.Invitation;
import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.user.domain.User;
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
