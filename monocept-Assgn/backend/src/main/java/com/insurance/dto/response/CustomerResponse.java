package com.insurance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String nomineeName;
    private String nomineeRelation;
}
