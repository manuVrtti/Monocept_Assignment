package com.insurance.service;

import com.insurance.dto.request.CustomerRequest;
import com.insurance.dto.response.CustomerResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.entity.User;

public interface CustomerService {

    CustomerResponse createProfile(User currentUser, CustomerRequest request);

    CustomerResponse updateProfile(User currentUser, CustomerRequest request);

    CustomerResponse getMyProfile(User currentUser);

    PaginatedResponse<CustomerResponse> getAllCustomers(int page, int size,
                                                         String sortField, String sortDirection);

    CustomerResponse getCustomerById(Long id);
}
