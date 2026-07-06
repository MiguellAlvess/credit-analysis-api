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
    private final CreditDecisionProducer creditDecisionProducer;

    public CreditDecision makeDecision(
            CreditRequest creditRequest,
            CreditRequestStatus status) {
        if (CreditRequestStatus.APPROVED.equals(status)) {
            creditRequest.approve();
            CreditDecision decision = CreditDecision.approve(
                    creditRequest,
                    creditRequest.calculateMaximumAllowedAmount(),
                    "Credit approved");
            creditDecisionProducer.publish(decision);
            return decision;
        }
        if (CreditRequestStatus.REJECTED.equals(status)) {
            creditRequest.reject();

            CreditDecision decision = CreditDecision.reject(
                    creditRequest,
                    "Credit rejected");
            creditDecisionProducer.publish(decision);
            return decision;
        }
        if (CreditRequestStatus.MANUAL_REVIEW.equals(status)) {
            creditRequest.sendToManualReview();
            CreditDecision decision = CreditDecision.manualReview(
                    creditRequest,
                    "Manual review required");
            creditDecisionProducer.publish(decision);
            return decision;
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
