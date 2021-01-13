package com.colleful.server.domain.matching.service;

import com.colleful.server.domain.matching.domain.MatchingRequest;
import java.util.List;

public interface MatchingRequestService {

    Long request(Long receiverId, Long userId);

    List<MatchingRequest> getAllMatchingRequestsToMyTeam(Long userId);

    List<MatchingRequest> getAllMatchingRequestsFromMyTeam(Long userId);

    void accept(Long matchingId, Long userId);

    void refuse(Long matchingId, Long userId);

    void cancel(Long matchingId, Long userId);
}
