package com.insurance.controller;

import com.insurance.dto.request.AdminAgentPolicyIssueRequest;
import com.insurance.dto.request.CustomerPolicyPurchaseRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PolicyResponse;
import com.insurance.entity.User;
import com.insurance.entity.enums.PolicyStatus;
import com.insurance.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@CrossOrigin
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping("/purchase")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PolicyResponse>> purchasePolicy(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CustomerPolicyPurchaseRequest request) {
        log.info("API call: Customer purchase policy");
        PolicyResponse response = policyService.purchasePolicy(currentUser, request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Policy purchased successfully. Make a payment to activate it.", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<PolicyResponse>> issuePolicy(
            @Valid @RequestBody AdminAgentPolicyIssueRequest request) {
        log.info("API call: Issue policy by Admin/Agent");
        PolicyResponse response = policyService.issuePolicy(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Policy issued successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<PolicyResponse>>> getAllPolicies(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) PolicyStatus policyStatus,
            @RequestParam(required = false) Long customerId) {
        log.info("API call: List policies");
        PaginatedResponse<PolicyResponse> response = policyService.getAllPolicies(currentUser, page, size, sortField, sortDirection, policyStatus, customerId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Policies retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyResponse>> getPolicyById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        log.info("API call: Get policy by ID: {}", id);
        PolicyResponse response = policyService.getPolicyById(currentUser, id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Policy retrieved successfully", response, LocalDateTime.now())
        );
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<PolicyResponse>> cancelPolicy(@PathVariable Long id) {
        log.info("API call: Cancel policy ID: {}", id);
        PolicyResponse response = policyService.cancelPolicy(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Policy cancelled successfully", response, LocalDateTime.now())
        );
    }
}
