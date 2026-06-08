package com.insurance.service;

import com.insurance.dto.request.PaymentRequest;
import com.insurance.dto.response.PaginatedResponse;
import com.insurance.dto.response.PaymentResponse;
import com.insurance.entity.User;
import com.insurance.entity.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    PaymentResponse recordPayment(User currentUser, PaymentRequest request);

    PaginatedResponse<PaymentResponse> getAllPayments(User currentUser, int page, int size,
                                                       String sortField, String sortDirection,
                                                       Long policyId, PaymentStatus paymentStatus);

    List<PaymentResponse> getPaymentsByPolicyId(User currentUser, Long policyId);
}
