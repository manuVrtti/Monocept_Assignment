package com.insurance.service.impl;

import com.insurance.dto.request.PlanRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PlanResponse;
import com.insurance.entity.InsuranceProduct;
import com.insurance.entity.PolicyPlan;
import com.insurance.exception.*;
import com.insurance.repository.InsuranceProductRepository;
import com.insurance.repository.PolicyPlanRepository;
import com.insurance.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PolicyPlanRepository planRepository;
    private final InsuranceProductRepository productRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "planName", "coverageAmount", "premiumAmount", "premiumType",
            "duration", "active", "createdDate", "updatedDate"
    );

    @Override
    @Transactional
    public PlanResponse createPlan(PlanRequest request) {
        log.info("Creating policy plan: {}", request.getPlanName());

        InsuranceProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        if (!product.isActive()) {
            throw new InvalidBusinessStateException("Cannot create plan under inactive product: " + product.getProductName());
        }

        validatePlanAmounts(request);

        PolicyPlan plan = PolicyPlan.builder()
                .insuranceProduct(product)
                .planName(request.getPlanName())
                .coverageAmount(request.getCoverageAmount())
                .premiumAmount(request.getPremiumAmount())
                .premiumType(request.getPremiumType())
                .duration(request.getDuration())
                .termsAndConditions(request.getTermsAndConditions())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        plan = planRepository.save(plan);
        log.info("Policy plan created successfully with ID: {}", plan.getId());
        return mapToPlanResponse(plan);
    }

    @Override
    @Transactional
    public PlanResponse updatePlan(Long id, PlanRequest request) {
        log.info("Updating plan with ID: {}", id);

        PolicyPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));

        InsuranceProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        validatePlanAmounts(request);

        plan.setInsuranceProduct(product);
        plan.setPlanName(request.getPlanName());
        plan.setCoverageAmount(request.getCoverageAmount());
        plan.setPremiumAmount(request.getPremiumAmount());
        plan.setPremiumType(request.getPremiumType());
        plan.setDuration(request.getDuration());
        plan.setTermsAndConditions(request.getTermsAndConditions());
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }

        plan = planRepository.save(plan);
        log.info("Plan updated successfully with ID: {}", id);
        return mapToPlanResponse(plan);
    }

    @Override
    public PaginatedResponse<PlanResponse> getAllPlans(int page, int size, String sortField,
                                                        String sortDirection, Long productId, Boolean active) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PolicyPlan> planPage = planRepository.findAllWithFilters(productId, active, pageable);

        List<PlanResponse> plans = planPage.getContent().stream()
                .map(this::mapToPlanResponse)
                .toList();

        return PaginatedResponse.<PlanResponse>builder()
                .records(plans)
                .currentPage(planPage.getNumber())
                .pageSize(planPage.getSize())
                .totalRecords(planPage.getTotalElements())
                .totalPages(planPage.getTotalPages())
                .lastPage(planPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Override
    public PlanResponse getPlanById(Long id) {
        log.info("Fetching plan by ID: {}", id);

        PolicyPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));

        return mapToPlanResponse(plan);
    }

    @Override
    public List<PlanResponse> getPlansByProductId(Long productId) {
        log.info("Fetching plans for product ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }

        return planRepository.findByInsuranceProductId(productId).stream()
                .map(this::mapToPlanResponse)
                .toList();
    }

    @Override
    @Transactional
    public PlanResponse deactivatePlan(Long id) {
        log.info("Deactivating plan with ID: {}", id);

        PolicyPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));

        plan.setActive(false);
        plan = planRepository.save(plan);
        log.info("Plan deactivated successfully with ID: {}", id);
        return mapToPlanResponse(plan);
    }

    private void validatePlanAmounts(PlanRequest request) {
        if (request.getCoverageAmount().compareTo(request.getPremiumAmount()) <= 0) {
            throw new InvalidBusinessStateException("Coverage amount must be greater than premium amount");
        }
    }

    private PlanResponse mapToPlanResponse(PolicyPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .productName(plan.getInsuranceProduct().getProductName())
                .productType(plan.getInsuranceProduct().getProductType().name())
                .planName(plan.getPlanName())
                .coverageAmount(plan.getCoverageAmount())
                .premiumAmount(plan.getPremiumAmount())
                .premiumType(plan.getPremiumType().name())
                .duration(plan.getDuration())
                .termsAndConditions(plan.getTermsAndConditions())
                .active(plan.isActive())
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
