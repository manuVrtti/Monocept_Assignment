package com.insurance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusHistoryResponse {

    private Long id;
    private String claimNumber;
    private String previousStatus;
    private String newStatus;
    private String remarks;
    private String updatedByName;
    private LocalDateTime updatedDate;
}
