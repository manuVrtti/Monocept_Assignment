package com.insurance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private Long id;
    private String productName;
    private String productType;
    private String planName;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private String premiumType;
    private Integer duration;
    private String termsAndConditions;
    private boolean active;
}
