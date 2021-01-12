package com.colleful.server.domain.team.service;

import com.colleful.server.domain.team.domain.Team;
import com.colleful.server.domain.team.domain.TeamStatus;
import com.colleful.server.domain.team.dto.TeamDto;
import com.colleful.server.domain.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {

    Long createTeam(Long leaderId, TeamDto.Request dto);

    Team getTeam(Long teamId);

    Team getTeam(Long teamId, Long userId);

    Page<Team> getAllReadyTeams(Pageable pageable);

    Page<Team> searchTeams(Pageable pageable, String teamName);

    List<User> getMembers(Long teamId);

    void updateStatus(Long teamId, Long userId, TeamStatus status);

    void leaveTeam(Long userId);

    void deleteTeam(Long userId);

    void finishMatch(Long userId);
}
