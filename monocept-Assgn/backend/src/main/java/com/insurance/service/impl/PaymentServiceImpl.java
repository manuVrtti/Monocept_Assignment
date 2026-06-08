package com.insurance.service.impl;

import com.insurance.dto.request.PaymentRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PaymentResponse;
import com.insurance.entity.Customer;
import com.insurance.entity.Policy;
import com.insurance.entity.PremiumPayment;
import com.insurance.entity.User;
import com.insurance.entity.enums.PaymentStatus;
import com.insurance.entity.enums.PolicyStatus;
import com.insurance.entity.enums.Role;
import com.insurance.exception.*;
import com.insurance.repository.CustomerRepository;
import com.insurance.repository.PolicyRepository;
import com.insurance.repository.PremiumPaymentRepository;
import com.insurance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PremiumPaymentRepository paymentRepository;
    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "amount", "paymentDate", "paymentMode", "transactionReference", "paymentStatus", "createdDate"
    );

    @Override
    @Transactional
    public PaymentResponse recordPayment(User currentUser, PaymentRequest request) {
        log.info("Recording payment for policy ID: {} by user: {}", request.getPolicyId(), currentUser.getEmail());

        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + request.getPolicyId()));

        // Check if customer can only pay for own policies
        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            if (!policy.getCustomer().getId().equals(customer.getId())) {
                throw new UnauthorizedAccessException("You can only make payments for your own policies");
            }
        }

        // Validate business rules
        if (policy.getPolicyStatus() == PolicyStatus.CANCELLED) {
            throw new InvalidPolicyStatusException("Cannot record payment for a cancelled policy");
        }
        if (policy.getPolicyStatus() == PolicyStatus.EXPIRED) {
            throw new InvalidPolicyStatusException("Cannot record payment for an expired policy");
        }

        if (paymentRepository.existsByTransactionReference(request.getTransactionReference())) {
            throw new DuplicateResourceException("Transaction reference already exists: " + request.getTransactionReference());
        }

        PremiumPayment payment = PremiumPayment.builder()
                .policy(policy)
                .amount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMode(request.getPaymentMode())
                .transactionReference(request.getTransactionReference())
                .paymentStatus(request.getPaymentStatus())
                .build();

        payment = paymentRepository.save(payment);

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            BigDecimal currentTotal = policy.getTotalPremiumPaid() != null ? policy.getTotalPremiumPaid() : BigDecimal.ZERO;
            policy.setTotalPremiumPaid(currentTotal.add(payment.getAmount()));

            // First successful payment >= required plan premium activates the policy
            if (policy.getPolicyStatus() == PolicyStatus.PENDING_PAYMENT &&
                    policy.getTotalPremiumPaid().compareTo(policy.getPolicyPlan().getPremiumAmount()) >= 0) {
                policy.setPolicyStatus(PolicyStatus.ACTIVE);
                log.info("Policy {} status updated to ACTIVE", policy.getPolicyNumber());
            }
            policyRepository.save(policy);
        }

        log.info("Payment recorded successfully with ID: {}", payment.getId());
        return mapToPaymentResponse(payment);
    }

    @Override
    public PaginatedResponse<PaymentResponse> getAllPayments(User currentUser, int page, int size,
                                                              String sortField, String sortDirection,
                                                              Long policyId, PaymentStatus paymentStatus) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PremiumPayment> paymentPage;

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            paymentPage = paymentRepository.findByCustomerIdWithFilters(customer.getId(), policyId, paymentStatus, pageable);
        } else {
            paymentPage = paymentRepository.findAllWithFilters(policyId, paymentStatus, pageable);
        }

        List<PaymentResponse> payments = paymentPage.getContent().stream()
                .map(this::mapToPaymentResponse)
                .toList();

        return PaginatedResponse.<PaymentResponse>builder()
                .records(payments)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalRecords(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .lastPage(paymentPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Override
    public List<PaymentResponse> getPaymentsByPolicyId(User currentUser, Long policyId) {
        log.info("Fetching payments for policy ID: {}", policyId);

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with ID: " + policyId));

        if (currentUser.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
            if (!policy.getCustomer().getId().equals(customer.getId())) {
                throw new UnauthorizedAccessException("You can only view payments for your own policies");
            }
        }

        List<PremiumPayment> payments = paymentRepository.findByPolicyIdOrderByPaymentDateDesc(policyId);
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    private PaymentResponse mapToPaymentResponse(PremiumPayment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .policyNumber(payment.getPolicy().getPolicyNumber())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMode(payment.getPaymentMode().name())
                .transactionReference(payment.getTransactionReference())
                .paymentStatus(payment.getPaymentStatus().name())
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
