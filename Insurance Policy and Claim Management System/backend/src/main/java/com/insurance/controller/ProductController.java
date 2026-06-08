package com.insurance.controller;

import com.insurance.dto.request.ProductRequest;
import com.insurance.dto.response.ApiResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.ProductResponse;
import com.insurance.entity.enums.ProductType;
import com.insurance.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("API call: Create product");
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Product created successfully", response, LocalDateTime.now()),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("API call: Update product ID: {}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product updated successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) Boolean active) {
        log.info("API call: List all products");
        PaginatedResponse<ProductResponse> response = productService.getAllProducts(page, size, sortField, sortDirection, productType, active);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products retrieved successfully", response, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        log.info("API call: Get product by ID: {}", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product retrieved successfully", response, LocalDateTime.now())
        );
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> deactivateProduct(@PathVariable Long id) {
        log.info("API call: Deactivate product ID: {}", id);
        ProductResponse response = productService.deactivateProduct(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product deactivated successfully", response, LocalDateTime.now())
        );
    }
}
