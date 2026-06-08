package com.insurance.service;

import com.insurance.dto.request.CreateAgentRequest;
import com.insurance.dto.request.LoginRequest;
import com.insurance.dto.request.RegisterRequest;
import com.insurance.dto.request.UserStatusUpdateRequest;
import com.insurance.dto.response.LoginResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.UserResponse;
import com.insurance.entity.enums.Role;

public interface UserService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse createAgent(CreateAgentRequest request);

    UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request);

    PaginatedResponse<UserResponse> getAllUsers(int page, int size, String sortField,
                                                 String sortDirection, Role role, Boolean active);
}
