package br.com.miguelalves.credit_analysis_api.request.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyService;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreateCreditRequestRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.mapper.CreditRequestMapper;
import br.com.miguelalves.credit_analysis_api.request.repository.CreditRequestRepository;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditRequestService {

    private final CompanyService companyService;
    private final CreditRequestRepository creditRequestRepository;

    @Transactional
    public CreditRequestResponse createCreditRequest(CreateCreditRequestRequest request) {
        Company company = companyService.findCompanyById(request.companyId());
        CreditRequest creditRequest = CreditRequest.create(
                company,
                request.requestedAmount(),
                request.annualRevenue());
        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);
        return CreditRequestMapper.fromCreditRequestToResponse(savedCreditRequest);
    }

    @Transactional(readOnly = true)
    public CreditRequestResponse getCreditRequestById(UUID id) {
        CreditRequest creditRequest = creditRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit request not found"));
        return CreditRequestMapper.fromCreditRequestToResponse(creditRequest);
    }
}
