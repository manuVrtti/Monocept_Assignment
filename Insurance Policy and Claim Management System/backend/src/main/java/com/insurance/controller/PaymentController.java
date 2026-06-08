package com.insurance.controller;

import com.insurance.dto.request.PaymentRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PaymentResponse;
import com.insurance.entity.User;
import com.insurance.entity.enums.PaymentStatus;
import com.insurance.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody PaymentRequest request) {
        log.info("API call: Record premium payment");
        PaymentResponse response = paymentService.recordPayment(currentUser, request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Payment recorded successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<PaymentResponse>>> getAllPayments(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) Long policyId,
            @RequestParam(required = false) PaymentStatus paymentStatus) {
        log.info("API call: List payments");
        PaginatedResponse<PaymentResponse> response = paymentService.getAllPayments(currentUser, page, size, sortField, sortDirection, policyId, paymentStatus);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payments retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByPolicyId(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long policyId) {
        log.info("API call: Get payments for policy ID: {}", policyId);
        List<PaymentResponse> response = paymentService.getPaymentsByPolicyId(currentUser, policyId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payments retrieved successfully for policy", response, LocalDateTime.now())
        );
    }
}
