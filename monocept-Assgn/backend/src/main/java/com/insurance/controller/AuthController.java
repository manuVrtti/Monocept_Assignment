package com.insurance.controller;

import com.insurance.dto.request.LoginRequest;
import com.insurance.dto.request.RegisterRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.LoginResponse;
import com.insurance.dto.response.UserResponse;
import com.insurance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("API call: Registering new customer");
        UserResponse response = userService.register(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Registration successful", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("API call: User login");
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", response, LocalDateTime.now())
        );
    }
}
