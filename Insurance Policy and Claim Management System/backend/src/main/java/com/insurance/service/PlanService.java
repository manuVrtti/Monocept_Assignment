package com.insurance.service;

import com.insurance.dto.request.PlanRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PlanResponse;

import java.util.List;

public interface PlanService {

    PlanResponse createPlan(PlanRequest request);

    PlanResponse updatePlan(Long id, PlanRequest request);

    PaginatedResponse<PlanResponse> getAllPlans(int page, int size, String sortField,
                                                 String sortDirection, Long productId, Boolean active);

    PlanResponse getPlanById(Long id);

    List<PlanResponse> getPlansByProductId(Long productId);

    PlanResponse deactivatePlan(Long id);
}
