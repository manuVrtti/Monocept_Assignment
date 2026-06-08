package com.insurance.service;

import com.insurance.dto.request.ProductRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.ProductResponse;
import com.insurance.entity.enums.ProductType;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    PaginatedResponse<ProductResponse> getAllProducts(int page, int size, String sortField,
                                                       String sortDirection, ProductType productType, Boolean active);

    ProductResponse getProductById(Long id);

    ProductResponse deactivateProduct(Long id);
}
