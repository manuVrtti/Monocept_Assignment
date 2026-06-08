package com.insurance.service.impl;

import com.insurance.dto.request.ProductRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.ProductResponse;
import com.insurance.entity.InsuranceProduct;
import com.insurance.entity.enums.ProductType;
import com.insurance.exception.*;
import com.insurance.repository.InsuranceProductRepository;
import com.insurance.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private final InsuranceProductRepository productRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "productName", "productType", "active", "createdDate", "updatedDate"
    );

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating insurance product: {}", request.getProductName());

        if (productRepository.existsByProductName(request.getProductName())) {
            throw new DuplicateResourceException("Product already exists with name: " + request.getProductName());
        }

        InsuranceProduct product = InsuranceProduct.builder()
                .productName(request.getProductName())
                .productType(request.getProductType())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        product = productRepository.save(product);
        log.info("Product created successfully with ID: {}", product.getId());
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);

        InsuranceProduct product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Check for duplicate name (if name is changed)
        if (!product.getProductName().equals(request.getProductName()) &&
                productRepository.existsByProductName(request.getProductName())) {
            throw new DuplicateResourceException("Product already exists with name: " + request.getProductName());
        }

        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setDescription(request.getDescription());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        product = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);
        return mapToProductResponse(product);
    }

    @Override
    public PaginatedResponse<ProductResponse> getAllProducts(int page, int size, String sortField,
                                                               String sortDirection, ProductType productType,
                                                               Boolean active) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InsuranceProduct> productPage = productRepository.findAllWithFilters(productType, active, pageable);

        List<ProductResponse> products = productPage.getContent().stream()
                .map(this::mapToProductResponse)
                .toList();

        return PaginatedResponse.<ProductResponse>builder()
                .records(products)
                .currentPage(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalRecords(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .lastPage(productPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product by ID: {}", id);

        InsuranceProduct product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse deactivateProduct(Long id) {
        log.info("Deactivating product with ID: {}", id);

        InsuranceProduct product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setActive(false);
        product = productRepository.save(product);
        log.info("Product deactivated successfully with ID: {}", id);
        return mapToProductResponse(product);
    }

    private ProductResponse mapToProductResponse(InsuranceProduct product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productType(product.getProductType().name())
                .description(product.getDescription())
                .active(product.isActive())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
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
