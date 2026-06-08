package com.insurance.service.impl;

import com.insurance.config.JwtService;
import com.insurance.dto.request.CreateAgentRequest;
import com.insurance.dto.request.LoginRequest;
import com.insurance.dto.request.RegisterRequest;
import com.insurance.dto.request.UserStatusUpdateRequest;
import com.insurance.dto.response.LoginResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.UserResponse;
import com.insurance.entity.User;
import com.insurance.entity.enums.Role;
import com.insurance.exception.*;
import com.insurance.repository.UserRepository;
import com.insurance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "fullName", "email", "role", "active", "createdDate", "updatedDate"
    );

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .role(Role.CUSTOMER)
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("Customer registered successfully with ID: {}", user.getId());
        return mapToUserResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            log.warn("Inactive user attempted login: {}", request.getEmail());
            throw new InactiveUserException("Your account has been deactivated. Please contact the administrator.");
        }

        String token = jwtService.generateToken(user);
        log.info("User logged in successfully: {}", request.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public UserResponse createAgent(CreateAgentRequest request) {
        log.info("Creating agent account with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User agent = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .role(Role.AGENT)
                .active(true)
                .build();

        agent = userRepository.save(agent);
        log.info("Agent account created successfully with ID: {}", agent.getId());
        return mapToUserResponse(agent);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        log.info("Updating user status for user ID: {} to active: {}", userId, request.getActive());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setActive(request.getActive());
        user = userRepository.save(user);
        log.info("User status updated successfully for user ID: {}", userId);
        return mapToUserResponse(user);
    }

    @Override
    public PaginatedResponse<UserResponse> getAllUsers(int page, int size, String sortField,
                                                        String sortDirection, Role role, Boolean active) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAllWithFilters(role, active, pageable);

        List<UserResponse> users = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .toList();

        return PaginatedResponse.<UserResponse>builder()
                .records(users)
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalRecords(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .lastPage(userPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdDate(user.getCreatedDate())
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
