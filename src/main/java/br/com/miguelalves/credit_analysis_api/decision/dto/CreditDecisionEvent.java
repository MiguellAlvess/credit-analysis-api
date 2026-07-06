package br.com.miguelalves.credit_analysis_api.decision.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecisionType;

public record CreditDecisionEvent(
        UUID creditRequestId,
        String companyName,
        String cnpj,
        CreditDecisionType decision,
        BigDecimal approvedAmount,
        String reason,
        LocalDateTime decidedAt) {
}
