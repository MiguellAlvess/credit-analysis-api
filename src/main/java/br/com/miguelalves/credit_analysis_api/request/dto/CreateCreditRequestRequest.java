package br.com.miguelalves.credit_analysis_api.request.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCreditRequestRequest(
                @NotNull(message = "Company id is required") UUID companyId,
                @NotNull(message = "Requested amount is required") @Positive(message = "Requested amount must be greater than zero") BigDecimal requestedAmount,
                @NotNull(message = "Annual revenue is required") @Positive(message = "Annual revenue must be greater than zero") BigDecimal annualRevenue) {
}
