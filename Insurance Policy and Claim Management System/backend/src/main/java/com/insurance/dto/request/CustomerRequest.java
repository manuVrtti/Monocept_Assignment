package com.insurance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Pin code is required")
    @Size(min = 5, max = 10, message = "Pin code must be between 5 and 10 characters")
    private String pinCode;

    @NotBlank(message = "Nominee name is required")
    @Size(max = 100, message = "Nominee name must not exceed 100 characters")
    private String nomineeName;

    @NotBlank(message = "Nominee relation is required")
    @Size(max = 50, message = "Nominee relation must not exceed 50 characters")
    private String nomineeRelation;
}
