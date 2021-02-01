package com.colleful.server.team.service;

import com.colleful.server.team.domain.Team;

public interface TeamServiceForService {

    Team getTeam(Long teamId);

    Team getUserTeam(Long userId);
}
