package br.com.miguelalves.credit_analysis_api.decision.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.dto.CreditDecisionResponse;
import br.com.miguelalves.credit_analysis_api.decision.mapper.CreditDecisionMapper;
import br.com.miguelalves.credit_analysis_api.decision.repository.CreditDecisionRepository;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditDecisionService {

    private final CreditDecisionRepository creditDecisionRepository;

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

    @Transactional(readOnly = true)
    public CreditDecisionResponse getDecisionByRequestId(UUID requestId) {
        CreditDecision decision = creditDecisionRepository
                .findByCreditRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit decision not found"));
        return CreditDecisionMapper.fromDecisionToResponse(decision);
    }
}
