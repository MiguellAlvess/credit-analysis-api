package br.com.miguelalves.credit_analysis_api.policy.service;

import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

@Service
public class CreditPolicyService {

    public CreditRequestStatus evaluate(CreditRequest creditRequest) {
        if (creditRequest.getRiskLevel() == RiskLevel.HIGH) {
            return CreditRequestStatus.REJECTED;
        }
        if (creditRequest.getRiskLevel() == RiskLevel.MEDIUM) {
            return CreditRequestStatus.MANUAL_REVIEW;
        }
        if (creditRequest.exceedsMaximumAllowedAmount()) {
            return CreditRequestStatus.REJECTED;
        }
        return CreditRequestStatus.APPROVED;
    }
}
