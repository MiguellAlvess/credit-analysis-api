package br.com.miguelalves.credit_analysis_api.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

public record CreditRequestResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String cnpj,
        BigDecimal requestedAmount,
        BigDecimal annualRevenue,
        Integer score,
        RiskLevel riskLevel,
        BigDecimal approvedLimit,
        CreditRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
