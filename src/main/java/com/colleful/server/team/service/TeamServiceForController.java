package com.colleful.server.team.service;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamServiceForController {

    Team createTeam(Long clientId, String teamName);

    Team getTeam(Long clientId, Long teamId);

    Page<Team> getAllReadyTeams(Pageable pageable);

    Page<Team> searchTeams(Pageable pageable, String teamName);

    List<User> getMembers(Long teamId);

    void changeStatus(Long clientId, TeamStatus status);

    void leaveTeam(Long clientId);

    void deleteTeam(Long clientId);

    void finishMatch(Long clientId);
}
