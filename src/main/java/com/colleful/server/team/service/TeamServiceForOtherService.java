package com.colleful.server.team.service;

import com.colleful.server.team.domain.Team;

public interface TeamServiceForOtherService {

    Team getTeamIfExist(Long teamId);

    Team getUserTeam(Long userId);
}
