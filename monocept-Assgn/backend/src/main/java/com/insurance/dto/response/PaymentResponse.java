package com.insurance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String policyNumber;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMode;
    private String transactionReference;
    private String paymentStatus;
}
