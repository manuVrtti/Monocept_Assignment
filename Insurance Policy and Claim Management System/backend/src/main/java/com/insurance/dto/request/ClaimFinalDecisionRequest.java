package com.insurance.dto.request;

import com.insurance.entity.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimFinalDecisionRequest {

    @NotNull(message = "Final status is required")
    private ClaimStatus finalStatus;

    private String remarks;
}
