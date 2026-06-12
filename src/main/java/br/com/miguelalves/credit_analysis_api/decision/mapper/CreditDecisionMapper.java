package br.com.miguelalves.credit_analysis_api.decision.mapper;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.dto.CreditDecisionResponse;

public class CreditDecisionMapper {

    public static CreditDecisionResponse fromDecisionToResponse(
            CreditDecision decision) {
        return new CreditDecisionResponse(
                decision.getId(),
                decision.getCreditRequest().getId(),
                decision.getDecision(),
                decision.getApprovedAmount(),
                decision.getReason(),
                decision.getDecidedAt());
    }

    private CreditDecisionMapper() {
    }
}
