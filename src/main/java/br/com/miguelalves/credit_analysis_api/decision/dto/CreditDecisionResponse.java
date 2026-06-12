package br.com.miguelalves.credit_analysis_api.decision.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecisionType;

public record CreditDecisionResponse(
        UUID id,
        UUID creditRequestId,
        CreditDecisionType decision,
        BigDecimal approvedAmount,
        String reason,
        LocalDateTime decidedAt) {
}
