package com.insurance.service;

import com.insurance.dto.request.ClaimFinalDecisionRequest;
import com.insurance.dto.request.ClaimRequest;
import com.insurance.dto.request.ClaimReviewRequest;
import com.insurance.dto.response.*;
import com.insurance.entity.User;
import com.insurance.entity.enums.ClaimStatus;

import java.util.List;

public interface ClaimService {

    ClaimResponse submitClaim(User currentUser, ClaimRequest request);

    ClaimResponse reviewClaim(User currentUser, Long claimId, ClaimReviewRequest request);

    ClaimResponse decideClaim(User currentUser, Long claimId, ClaimFinalDecisionRequest request);

    PaginatedResponse<ClaimResponse> getAllClaims(User currentUser, int page, int size,
                                                   String sortField, String sortDirection,
                                                   ClaimStatus claimStatus, Long customerId);

    ClaimResponse getClaimById(User currentUser, Long id);

    List<ClaimStatusHistoryResponse> getClaimHistory(User currentUser, Long claimId);
}
