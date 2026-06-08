package com.insurance.service.impl;

import com.insurance.dto.request.ClaimFinalDecisionRequest;
import com.insurance.dto.request.ClaimRequest;
import com.insurance.dto.request.ClaimReviewRequest;
import com.insurance.dto.response.*;
import com.insurance.entity.*;
import com.insurance.entity.enums.ClaimStatus;
import com.insurance.entity.enums.PolicyStatus;
import com.insurance.entity.enums.Role;
import com.insurance.exception.*;
import com.insurance.repository.*;
import com.insurance.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimDocumentRepository claimDocumentRepository;
    private final ClaimStatusHistoryRepository claimStatusHistoryRepository;
    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "claimNumber", "claimAmount", "incidentDate", "claimStatus", "createdDate", "updatedDate"
    );

    @Override
    @Transactional
    public ClaimResponse submitClaim(User currentUser, ClaimRequest request) {
        log.info("Submitting claim for policy ID: {} by user: {}", request.getPolicyId(), currentUser.getEmail());

        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + request.getPolicyId()));

        // Check if customer owns the policy
        Customer customer = customerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedAccessException("You can only submit claims for your own policies");
        }

        // Validate business rules
        if (policy.getPolicyStatus() != PolicyStatus.ACTIVE) {
            throw new InvalidPolicyStatusException("Claims can only be filed against active policies. Current status: " + policy.getPolicyStatus());
        }

        if (request.getIncidentDate().isAfter(LocalDate.now())) {
            throw new InvalidBusinessStateException("Incident date cannot be in the future");
        }

        if (request.getClaimAmount().compareTo(policy.getPolicyPlan().getCoverageAmount()) > 0) {
            throw new ClaimAmountExceededException("Claim amount " + request.getClaimAmount() +
                    " exceeds policy coverage amount of " + policy.getPolicyPlan().getCoverageAmount());
        }

        if (request.getDocuments() == null || request.getDocuments().isEmpty()) {
            throw new InvalidBusinessStateException("At least one claim document is required");
        }

        String claimNumber = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Claim claim = Claim.builder()
                .claimNumber(claimNumber)
                .policy(policy)
                .claimAmount(request.getClaimAmount())
                .claimReason(request.getClaimReason())
                .incidentDate(request.getIncidentDate())
                .claimStatus(ClaimStatus.SUBMITTED)
                .build();

        claim = claimRepository.save(claim);

        // Save documents
        final Claim savedClaim = claim;
        List<ClaimDocument> documents = request.getDocuments().stream()
                .map(docReq -> ClaimDocument.builder()
                        .claim(savedClaim)
                        .documentName(docReq.getDocumentName())
                        .documentType(docReq.getDocumentType())
                        .documentReference(docReq.getDocumentReference())
                        .build())
                .toList();

        claimDocumentRepository.saveAll(documents);
        savedClaim.setDocuments(documents);

        // Save status history
        ClaimStatusHistory history = ClaimStatusHistory.builder()
                .claim(savedClaim)
                .previousStatus(null)
                .newStatus(ClaimStatus.SUBMITTED)
                .remarks("Claim submitted by customer")
                .updatedBy(currentUser)
                .build();
        claimStatusHistoryRepository.save(history);

        log.info("Claim submitted successfully with claim number: {}", claim.getClaimNumber());
        return mapToClaimResponse(savedClaim);
    }

    @Override
    @Transactional
    public ClaimResponse reviewClaim(User currentUser, Long claimId, ClaimReviewRequest request) {
        log.info("Agent {} reviewing claim ID: {} with recommended status: {}",
                currentUser.getEmail(), claimId, request.getRecommendedStatus());

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        if (claim.getClaimStatus() == ClaimStatus.APPROVED || claim.getClaimStatus() == ClaimStatus.REJECTED) {
            throw new InvalidClaimStatusException("Finalized claims cannot be modified");
        }

        ClaimStatus oldStatus = claim.getClaimStatus();
        ClaimStatus newStatus = request.getRecommendedStatus();

        if (newStatus != ClaimStatus.UNDER_REVIEW &&
                newStatus != ClaimStatus.RECOMMENDED_APPROVAL &&
                newStatus != ClaimStatus.RECOMMENDED_REJECTION) {
            throw new InvalidClaimStatusException("Agents can only set claim status to UNDER_REVIEW, RECOMMENDED_APPROVAL, or RECOMMENDED_REJECTION");
        }

        claim.setClaimStatus(newStatus);
        claim.setAgentRemarks(request.getRemarks());
        claim = claimRepository.save(claim);

        // Record history
        ClaimStatusHistory history = ClaimStatusHistory.builder()
                .claim(claim)
                .previousStatus(oldStatus)
                .newStatus(newStatus)
                .remarks(request.getRemarks())
                .updatedBy(currentUser)
                .build();
        claimStatusHistoryRepository.save(history);

        log.info("Claim ID {} review completed. New status: {}", claimId, newStatus);
        return mapToClaimResponse(claim);
    }

    @Override
    @Transactional
    public ClaimResponse decideClaim(User currentUser, Long claimId, ClaimFinalDecisionRequest request) {
        log.info("Admin {} deciding claim ID: {} with status: {}",
                currentUser.getEmail(), claimId, request.getFinalStatus());

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        if (claim.getClaimStatus() == ClaimStatus.APPROVED || claim.getClaimStatus() == ClaimStatus.REJECTED) {
            throw new InvalidClaimStatusException("Finalized claims cannot be modified");
        }

        ClaimStatus oldStatus = claim.getClaimStatus();
        ClaimStatus newStatus = request.getFinalStatus();

        if (newStatus != ClaimStatus.APPROVED && newStatus != ClaimStatus.REJECTED) {
            throw new InvalidClaimStatusException("Admins can only set claim status to APPROVED or REJECTED");
        }

        claim.setClaimStatus(newStatus);
        claim.setAdminRemarks(request.getRemarks());
        claim = claimRepository.save(claim);

        // Record history
        ClaimStatusHistory history = ClaimStatusHistory.builder()
                .claim(claim)
                .previousStatus(oldStatus)
                .newStatus(newStatus)
                .remarks(request.getRemarks())
                .updatedBy(currentUser)
                .build();
        claimStatusHistoryRepository.save(history);

        log.info("Claim ID {} final decision made: {}", claimId, newStatus);
        return mapToClaimResponse(claim);
    }

    @Override
    public PaginatedResponse<ClaimResponse> getAllClaims(User currentUser, int page, int size,
                                                          String sortField, String sortDirection,
                                                          ClaimStatus claimStatus, Long customerId) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Claim> claimPage;

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            claimPage = claimRepository.findByCustomerIdWithFilters(customer.getId(), claimStatus, pageable);
        } else {
            claimPage = claimRepository.findAllWithFilters(claimStatus, customerId, pageable);
        }

        List<ClaimResponse> claims = claimPage.getContent().stream()
                .map(this::mapToClaimResponse)
                .toList();

        return PaginatedResponse.<ClaimResponse>builder()
                .records(claims)
                .currentPage(claimPage.getNumber())
                .pageSize(claimPage.getSize())
                .totalRecords(claimPage.getTotalElements())
                .totalPages(claimPage.getTotalPages())
                .lastPage(claimPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Override
    public ClaimResponse getClaimById(User currentUser, Long id) {
        log.info("Fetching claim by ID: {}", id);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + id));

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            if (!claim.getPolicy().getCustomer().getId().equals(customer.getId())) {
                throw new UnauthorizedAccessException("You can only view your own claims");
            }
        }

        return mapToClaimResponse(claim);
    }

    @Override
    public List<ClaimStatusHistoryResponse> getClaimHistory(User currentUser, Long claimId) {
        log.info("Fetching claim history for claim ID: {}", claimId);

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            if (!claim.getPolicy().getCustomer().getId().equals(customer.getId())) {
                throw new UnauthorizedAccessException("You can only view history of your own claims");
            }
        }

        List<ClaimStatusHistory> historyList = claimStatusHistoryRepository.findByClaimIdOrderByUpdatedDateDesc(claimId);
        return historyList.stream()
                .map(history -> ClaimStatusHistoryResponse.builder()
                        .id(history.getId())
                        .claimNumber(history.getClaim().getClaimNumber())
                        .previousStatus(history.getPreviousStatus() != null ? history.getPreviousStatus().name() : null)
                        .newStatus(history.getNewStatus().name())
                        .remarks(history.getRemarks())
                        .updatedByName(history.getUpdatedBy().getFullName())
                        .updatedDate(history.getUpdatedDate())
                        .build())
                .toList();
    }

    private ClaimResponse mapToClaimResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyNumber(claim.getPolicy().getPolicyNumber())
                .customerName(claim.getPolicy().getCustomer().getUser().getFullName())
                .claimAmount(claim.getClaimAmount())
                .claimReason(claim.getClaimReason())
                .incidentDate(claim.getIncidentDate())
                .claimStatus(claim.getClaimStatus().name())
                .agentRemarks(claim.getAgentRemarks())
                .adminRemarks(claim.getAdminRemarks())
                .createdDate(claim.getCreatedDate())
                .updatedDate(claim.getUpdatedDate())
                .build();
    }

    private void validatePagination(int page, int size, String sortField) {
        if (page < 0) {
            throw new InvalidPaginationException("Page number cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new InvalidPaginationException("Page size must be between 1 and 100");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new InvalidPaginationException("Invalid sort field: " + sortField +
                    ". Allowed fields: " + ALLOWED_SORT_FIELDS);
        }
    }
}
