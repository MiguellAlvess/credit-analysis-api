package br.com.miguelalves.credit_analysis_api.decision.service;

import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;

@Service
public class CreditDecisionService {

    public CreditDecision makeDecision(
            CreditRequest creditRequest,
            CreditRequestStatus status) {
        if (CreditRequestStatus.APPROVED.equals(status)) {
            creditRequest.approve();
            return CreditDecision.approve(
                    creditRequest,
                    creditRequest.calculateMaximumAllowedAmount(),
                    "Credit approved");
        }
        if (CreditRequestStatus.REJECTED.equals(status)) {
            creditRequest.reject();
            return CreditDecision.reject(
                    creditRequest,
                    "Credit rejected");
        }
        if (CreditRequestStatus.MANUAL_REVIEW.equals(status)) {
            creditRequest.sendToManualReview();
            return CreditDecision.manualReview(
                    creditRequest,
                    "Manual review required");
        }
        throw new BusinessException("Invalid credit request status");
    }
}
