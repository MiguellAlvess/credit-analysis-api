package br.com.miguelalves.credit_analysis_api.request.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyService;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyValidationService;
import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.repository.CreditDecisionRepository;
import br.com.miguelalves.credit_analysis_api.decision.service.CreditDecisionService;
import br.com.miguelalves.credit_analysis_api.policy.service.CreditPolicyService;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.request.dto.CreateCreditRequestRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.mapper.CreditRequestMapper;
import br.com.miguelalves.credit_analysis_api.request.repository.CreditRequestRepository;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;
import br.com.miguelalves.credit_analysis_api.score.service.ScoreCalculationService;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditRequestService {

    private final CompanyService companyService;
    private final CreditRequestRepository creditRequestRepository;
    private final CompanyValidationService companyValidationService;
    private final ScoreCalculationService scoreCalculationService;
    private final CreditPolicyService creditPolicyService;
    private final CreditDecisionService creditDecisionService;
    private final CreditDecisionRepository creditDecisionRepository;

    @Transactional
    public CreditRequestResponse createCreditRequest(CreateCreditRequestRequest request) {
        Company company = companyService.findCompanyById(request.companyId());
        Company validatedCompany = companyValidationService.validateCompany(company);
        CreditRequest creditRequest = CreditRequest.create(
                validatedCompany,
                request.requestedAmount(),
                request.annualRevenue());
        int score = scoreCalculationService.calculate(
                validatedCompany,
                creditRequest.getAnnualRevenue());
        RiskLevel riskLevel = scoreCalculationService.classifyRiskLevel(score);
        creditRequest.registerScore(score, riskLevel);
        CreditRequestStatus evaluatedStatus = creditPolicyService.evaluate(creditRequest);
        CreditDecision creditDecision = creditDecisionService.makeDecision(
                creditRequest,
                evaluatedStatus);
        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);
        creditDecisionRepository.save(creditDecision);
        return CreditRequestMapper.fromCreditRequestToResponse(savedCreditRequest);
    }

    @Transactional(readOnly = true)
    public CreditRequestResponse getCreditRequestById(UUID id) {
        CreditRequest creditRequest = creditRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit request not found"));
        return CreditRequestMapper.fromCreditRequestToResponse(creditRequest);
    }
}
