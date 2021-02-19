package com.colleful.server.team.service;

import com.colleful.server.team.domain.Team;
import com.colleful.server.team.domain.TeamStatus;
import com.colleful.server.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamServiceForController {

    Team createTeam(Long leaderId, String teamName);

    Team getTeam(Long teamId, Long userId);

    Page<Team> getAllReadyTeams(Pageable pageable);

    Page<Team> searchTeams(Pageable pageable, String teamName);

    List<User> getMembers(Long teamId);

    void updateStatus(Long teamId, Long userId, TeamStatus status);

    void removeMember(Long userId);

    void deleteTeam(Long userId);

    void finishMatch(Long userId);
}
