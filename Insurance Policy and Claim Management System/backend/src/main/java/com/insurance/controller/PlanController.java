package com.insurance.controller;

import com.insurance.dto.request.PlanRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PlanResponse;
import com.insurance.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(@Valid @RequestBody PlanRequest request) {
        log.info("API call: Create plan");
        PlanResponse response = planService.createPlan(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Plan created successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequest request) {
        log.info("API call: Update plan ID: {}", id);
        PlanResponse response = planService.updatePlan(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Plan updated successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<PlanResponse>>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Boolean active) {
        log.info("API call: List all plans");
        PaginatedResponse<PlanResponse> response = planService.getAllPlans(page, size, sortField, sortDirection, productId, active);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Plans retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanById(@PathVariable Long id) {
        log.info("API call: Get plan by ID: {}", id);
        PlanResponse response = planService.getPlanById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Plan retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlansByProductId(@PathVariable Long productId) {
        log.info("API call: Get plans by product ID: {}", productId);
        List<PlanResponse> response = planService.getPlansByProductId(productId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Plans for product retrieved successfully", response, LocalDateTime.now())
        );
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponse>> deactivatePlan(@PathVariable Long id) {
        log.info("API call: Deactivate plan ID: {}", id);
        PlanResponse response = planService.deactivatePlan(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Plan deactivated successfully", response, LocalDateTime.now())
        );
    }
}
