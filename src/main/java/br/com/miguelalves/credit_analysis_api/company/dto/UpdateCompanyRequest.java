package br.com.miguelalves.credit_analysis_api.company.dto;

import java.time.LocalDate;

import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @NotBlank(message = "Company name is required") String name,
        @NotNull(message = "Registration status is required") RegistrationStatus registrationStatus,
        @NotBlank(message = "Postal code is required") @Size(min = 8, max = 9, message = "Postal code must have between 8 and 9 characters") String postalCode,
        String city,
        @Size(min = 2, max = 2, message = "State must have 2 characters") String state,
        @NotNull(message = "Founded date is required") LocalDate foundedAt) {
}
