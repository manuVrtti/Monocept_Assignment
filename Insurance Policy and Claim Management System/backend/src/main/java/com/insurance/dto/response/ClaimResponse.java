package com.insurance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {

    private Long id;
    private String claimNumber;
    private String policyNumber;
    private String customerName;
    private BigDecimal claimAmount;
    private String claimReason;
    private LocalDate incidentDate;
    private String claimStatus;
    private String agentRemarks;
    private String adminRemarks;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
