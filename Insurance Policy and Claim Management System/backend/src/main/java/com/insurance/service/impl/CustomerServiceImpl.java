package com.insurance.service.impl;

import com.insurance.dto.request.CustomerRequest;
import com.insurance.dto.response.CustomerResponse;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.entity.Customer;
import com.insurance.entity.User;
import com.insurance.entity.enums.Role;
import com.insurance.exception.*;
import com.insurance.repository.CustomerRepository;
import com.insurance.service.CustomerService;
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
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "dateOfBirth", "city", "state", "createdDate", "updatedDate"
    );

    @Override
    @Transactional
    public CustomerResponse createProfile(User currentUser, CustomerRequest request) {
        log.info("Creating customer profile for user ID: {}", currentUser.getId());

        if (currentUser.getRole() != Role.CUSTOMER) {
            throw new InvalidBusinessStateException("Only users with CUSTOMER role can create a customer profile");
        }

        if (customerRepository.existsByUserId(currentUser.getId())) {
            throw new DuplicateResourceException("Customer profile already exists for user: " + currentUser.getEmail());
        }

        Customer customer = Customer.builder()
                .user(currentUser)
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pinCode(request.getPinCode())
                .nomineeName(request.getNomineeName())
                .nomineeRelation(request.getNomineeRelation())
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer profile created successfully with ID: {}", customer.getId());
        return mapToCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateProfile(User currentUser, CustomerRequest request) {
        log.info("Updating customer profile for user ID: {}", currentUser.getId());

        Customer customer = customerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + currentUser.getEmail()));

        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPinCode(request.getPinCode());
        customer.setNomineeName(request.getNomineeName());
        customer.setNomineeRelation(request.getNomineeRelation());

        customer = customerRepository.save(customer);
        log.info("Customer profile updated successfully for user ID: {}", currentUser.getId());
        return mapToCustomerResponse(customer);
    }

    @Override
    public CustomerResponse getMyProfile(User currentUser) {
        log.info("Fetching profile for user ID: {}", currentUser.getId());

        Customer customer = customerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found. Please create your profile first."));

        return mapToCustomerResponse(customer);
    }

    @Override
    public PaginatedResponse<CustomerResponse> getAllCustomers(int page, int size,
                                                                 String sortField, String sortDirection) {
        validatePagination(page, size, sortField);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        List<CustomerResponse> customers = customerPage.getContent().stream()
                .map(this::mapToCustomerResponse)
                .toList();

        return PaginatedResponse.<CustomerResponse>builder()
                .records(customers)
                .currentPage(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalRecords(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .lastPage(customerPage.isLast())
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        log.info("Fetching customer by ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        return mapToCustomerResponse(customer);
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        User user = customer.getUser();
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .dateOfBirth(customer.getDateOfBirth())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .pinCode(customer.getPinCode())
                .nomineeName(customer.getNomineeName())
                .nomineeRelation(customer.getNomineeRelation())
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
