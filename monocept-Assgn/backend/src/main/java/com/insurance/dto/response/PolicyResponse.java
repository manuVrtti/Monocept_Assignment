package com.insurance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    private Long id;
    private String policyNumber;
    private String customerName;
    private String planName;
    private String productType;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private String premiumType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String policyStatus;
    private BigDecimal totalPremiumPaid;
}
