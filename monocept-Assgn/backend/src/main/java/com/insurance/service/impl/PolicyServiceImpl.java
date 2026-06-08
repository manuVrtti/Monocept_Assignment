package com.insurance.service.impl;

import com.insurance.dto.request.AdminAgentPolicyIssueRequest;
import com.insurance.dto.request.CustomerPolicyPurchaseRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PolicyResponse;
import com.insurance.entity.Customer;
import com.insurance.entity.Policy;
import com.insurance.entity.PolicyPlan;
import com.insurance.entity.User;
import com.insurance.entity.enums.PolicyStatus;
import com.insurance.entity.enums.Role;
import com.insurance.exception.*;
import com.insurance.repository.CustomerRepository;
import com.insurance.repository.PolicyPlanRepository;
import com.insurance.repository.PolicyRepository;
import com.insurance.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final PolicyPlanRepository planRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "policyNumber", "startDate", "endDate", "policyStatus",
            "totalPremiumPaid", "createdDate", "updatedDate"
    );

    @Override
    @Transactional
    public PolicyResponse purchasePolicy(User currentUser, CustomerPolicyPurchaseRequest request) {
        log.info("Customer {} purchasing policy with plan ID: {}", currentUser.getEmail(), request.getPlanId());

        Customer customer = customerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new InvalidBusinessStateException(
                        "Customer profile must be created before purchasing a policy"));

        PolicyPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + request.getPlanId()));

        if (!plan.isActive()) {
            throw new InvalidBusinessStateException("Cannot purchase policy with inactive plan: " + plan.getPlanName());
        }

        if (!plan.getInsuranceProduct().isActive()) {
            throw new InvalidBusinessStateException("Cannot purchase policy with inactive product: " +
                    plan.getInsuranceProduct().getProductName());
        }

        Policy policy = createPolicy(customer, plan, request.getStartDate());
        policy = policyRepository.save(policy);
        log.info("Policy purchased successfully. Policy number: {}", policy.getPolicyNumber());
        return mapToPolicyResponse(policy);
    }

    @Override
    @Transactional
    public PolicyResponse issuePolicy(AdminAgentPolicyIssueRequest request) {
        log.info("Issuing policy for customer ID: {} with plan ID: {}", request.getCustomerId(), request.getPlanId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));

        PolicyPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + request.getPlanId()));

        if (!plan.isActive()) {
            throw new InvalidBusinessStateException("Cannot issue policy with inactive plan: " + plan.getPlanName());
        }

        if (!plan.getInsuranceProduct().isActive()) {
            throw new InvalidBusinessStateException("Cannot issue policy with inactive product: " +
                    plan.getInsuranceProduct().getProductName());
        }

        Policy policy = createPolicy(customer, plan, request.getStartDate());
        policy = policyRepository.save(policy);
        log.info("Policy issued successfully. Policy number: {}", policy.getPolicyNumber());
        return mapToPolicyResponse(policy);
    }

    @Override
    public PaginatedResponse<PolicyResponse> getAllPolicies(User currentUser, int page, int size,
                                                              String sortField, String sortDirection,
                                                              PolicyStatus policyStatus, Long customerId) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Policy> policyPage;

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            policyPage = policyRepository.findByCustomerIdWithFilters(customer.getId(), policyStatus, pageable);
        } else {
            policyPage = policyRepository.findAllWithFilters(policyStatus, customerId, pageable);
        }

        List<PolicyResponse> policies = policyPage.getContent().stream()
                .map(this::mapToPolicyResponse)
                .toList();

        return PaginatedResponse.<PolicyResponse>builder()
                .records(policies)
                .currentPage(policyPage.getNumber())
                .pageSize(policyPage.getSize())
                .totalRecords(policyPage.getTotalElements())
                .totalPages(policyPage.getTotalPages())
                .lastPage(policyPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Override
    public PolicyResponse getPolicyById(User currentUser, Long id) {
        log.info("Fetching policy by ID: {}", id);

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + id));

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            if (!policy.getCustomer().getId().equals(customer.getId())) {
                throw new UnauthorizedAccessException("You can only view your own policies");
            }
        }

        return mapToPolicyResponse(policy);
    }

    @Override
    @Transactional
    public PolicyResponse cancelPolicy(Long id) {
        log.info("Cancelling policy with ID: {}", id);

        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + id));

        if (policy.getPolicyStatus() == PolicyStatus.CANCELLED) {
            throw new InvalidPolicyStatusException("Policy is already cancelled");
        }

        if (policy.getPolicyStatus() == PolicyStatus.EXPIRED) {
            throw new InvalidPolicyStatusException("Cannot cancel an expired policy");
        }

        policy.setPolicyStatus(PolicyStatus.CANCELLED);
        policy = policyRepository.save(policy);
        log.info("Policy cancelled successfully. Policy number: {}", policy.getPolicyNumber());
        return mapToPolicyResponse(policy);
    }

    private Policy createPolicy(Customer customer, PolicyPlan plan, LocalDate startDate) {
        String policyNumber = "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate endDate = startDate.plusYears(plan.getDuration());

        return Policy.builder()
                .policyNumber(policyNumber)
                .customer(customer)
                .policyPlan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .policyStatus(PolicyStatus.PENDING_PAYMENT)
                .totalPremiumPaid(BigDecimal.ZERO)
                .build();
    }

    private PolicyResponse mapToPolicyResponse(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .customerName(policy.getCustomer().getUser().getFullName())
                .planName(policy.getPolicyPlan().getPlanName())
                .productType(policy.getPolicyPlan().getInsuranceProduct().getProductType().name())
                .coverageAmount(policy.getPolicyPlan().getCoverageAmount())
                .premiumAmount(policy.getPolicyPlan().getPremiumAmount())
                .premiumType(policy.getPolicyPlan().getPremiumType().name())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .policyStatus(policy.getPolicyStatus().name())
                .totalPremiumPaid(policy.getTotalPremiumPaid())
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
