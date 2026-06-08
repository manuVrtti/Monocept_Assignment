package com.insurance.controller;

import com.insurance.dto.request.ClaimFinalDecisionRequest;
import com.insurance.dto.request.ClaimRequest;
import com.insurance.dto.request.ClaimReviewRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.ClaimResponse;
import com.insurance.dto.response.ClaimStatusHistoryResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.entity.User;
import com.insurance.entity.enums.ClaimStatus;
import com.insurance.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@CrossOrigin
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ClaimResponse>> submitClaim(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ClaimRequest request) {
        log.info("API call: Customer submit claim");
        ClaimResponse response = claimService.submitClaim(currentUser, request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Claim submitted successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<ClaimResponse>> reviewClaim(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ClaimReviewRequest request) {
        log.info("API call: Agent review claim");
        ClaimResponse response = claimService.reviewClaim(currentUser, id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Claim recommendation updated successfully", response, LocalDateTime.now())
        );
    }

    @PutMapping("/{id}/decide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> decideClaim(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ClaimFinalDecisionRequest request) {
        log.info("API call: Admin decide claim");
        ClaimResponse response = claimService.decideClaim(currentUser, id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Claim decision processed successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ClaimResponse>>> getAllClaims(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) ClaimStatus claimStatus,
            @RequestParam(required = false) Long customerId) {
        log.info("API call: List claims");
        PaginatedResponse<ClaimResponse> response = claimService.getAllClaims(currentUser, page, size, sortField, sortDirection, claimStatus, customerId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Claims retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaimById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        log.info("API call: Get claim by ID: {}", id);
        ClaimResponse response = claimService.getClaimById(currentUser, id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Claim retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<ClaimStatusHistoryResponse>>> getClaimHistory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        log.info("API call: Get claim status history timeline");
        List<ClaimStatusHistoryResponse> response = claimService.getClaimHistory(currentUser, id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Claim status history retrieved successfully", response, LocalDateTime.now())
        );
    }
}
