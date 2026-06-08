package com.insurance.controller;

import com.insurance.dto.request.CustomerRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.CustomerResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.entity.User;
import com.insurance.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CustomerRequest request) {
        log.info("API call: Create customer profile for user: {}", currentUser.getEmail());
        CustomerResponse response = customerService.createProfile(currentUser, request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Customer profile created successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CustomerRequest request) {
        log.info("API call: Update customer profile for user: {}", currentUser.getEmail());
        CustomerResponse response = customerService.updateProfile(currentUser, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Customer profile updated successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(@AuthenticationPrincipal User currentUser) {
        log.info("API call: Get own profile for user: {}", currentUser.getEmail());
        CustomerResponse response = customerService.getMyProfile(currentUser);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Profile retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<PaginatedResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        log.info("API call: List all customers");
        PaginatedResponse<CustomerResponse> response = customerService.getAllCustomers(page, size, sortField, sortDirection);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Customers list retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        log.info("API call: Get customer by ID: {}", id);
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Customer details retrieved successfully", response, LocalDateTime.now())
        );
    }
}
