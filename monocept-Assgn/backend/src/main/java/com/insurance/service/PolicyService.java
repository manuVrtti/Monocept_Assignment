package com.insurance.service;

import com.insurance.dto.request.AdminAgentPolicyIssueRequest;
import com.insurance.dto.request.CustomerPolicyPurchaseRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PolicyResponse;
import com.insurance.entity.User;
import com.insurance.entity.enums.PolicyStatus;

public interface PolicyService {

    PolicyResponse purchasePolicy(User currentUser, CustomerPolicyPurchaseRequest request);

    PolicyResponse issuePolicy(AdminAgentPolicyIssueRequest request);

    PaginatedResponse<PolicyResponse> getAllPolicies(User currentUser, int page, int size,
                                                      String sortField, String sortDirection,
                                                      PolicyStatus policyStatus, Long customerId);

    PolicyResponse getPolicyById(User currentUser, Long id);

    PolicyResponse cancelPolicy(Long id);
}
