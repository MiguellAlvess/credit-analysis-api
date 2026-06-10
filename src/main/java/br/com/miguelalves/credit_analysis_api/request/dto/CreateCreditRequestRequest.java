package br.com.miguelalves.credit_analysis_api.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCreditRequestRequest(
        @NotBlank(message = "CNPJ is required") String cnpj,
        @NotBlank(message = "Company name is required") String companyName,
        @NotNull(message = "Registration status is required") RegistrationStatus registrationStatus,
        @NotBlank(message = "Postal code is required") String postalCode,
        String city,
        String state,
        LocalDate foundedAt,
        @NotNull(message = "Requested amount is required") @Positive(message = "Requested amount must be greater than zero") BigDecimal requestedAmount,
        @NotNull(message = "Annual revenue is required") @Positive(message = "Annual revenue must be greater than zero") BigDecimal annualRevenue) {
}
