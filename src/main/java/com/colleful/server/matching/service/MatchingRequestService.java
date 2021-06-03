package com.colleful.server.matching.service;

import com.colleful.server.matching.domain.MatchingRequest;
import java.util.List;

public interface MatchingRequestService {

    MatchingRequest request(Long senderId, Long receivedTeamId);

    List<MatchingRequest> getAllSentMatchingRequests(Long clientId);

    List<MatchingRequest> getAllReceivedMatchingRequests(Long clientId);

    void accept(Long clientId, Long matchingId);

    void refuse(Long clientId, Long matchingId);

    void cancel(Long clientId, Long matchingId);
}
