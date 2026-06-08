package com.insurance.controller;

import com.insurance.dto.request.CreateAgentRequest;
import com.insurance.dto.request.UserStatusUpdateRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.UserResponse;
import com.insurance.entity.enums.Role;
import com.insurance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active) {
        log.info("API call: Get all users with filters");
        PaginatedResponse<UserResponse> users = userService.getAllUsers(page, size, sortField, sortDirection, role, active);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users retrieved successfully", users, LocalDateTime.now())
        );
    }

    @PostMapping("/agent")
    public ResponseEntity<ApiResponse<UserResponse>> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        log.info("API call: Create agent");
        UserResponse response = userService.createAgent(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Agent account created successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        log.info("API call: Update status of user ID: {}", id);
        UserResponse response = userService.updateUserStatus(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User status updated successfully", response, LocalDateTime.now())
        );
    }
}
