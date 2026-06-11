package br.com.miguelalves.credit_analysis_api.request.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
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

    private final CompanyRepository companyRepository;
    private final CreditRequestRepository creditRequestRepository;

    public CreditRequestResponse getCreditRequestById(UUID id) {
        CreditRequest creditRequest = creditRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit request not found"));
        return CreditRequestMapper.fromCreditRequestToResponse(creditRequest);
    }

    @Transactional
    public CreditRequestResponse createCreditRequest(CreateCreditRequestRequest request) {
        Company company = findOrCreateCompany(request);
        CreditRequest creditRequest = CreditRequest.create(
                company,
                request.requestedAmount(),
                request.annualRevenue());
        CreditRequest savedCreditRequest = creditRequestRepository.save(creditRequest);
        return CreditRequestMapper.fromCreditRequestToResponse(savedCreditRequest);
    }

    private Company findOrCreateCompany(CreateCreditRequestRequest request) {
        return companyRepository.findByCnpj(request.cnpj())
                .map(company -> updateCompany(company, request))
                .orElseGet(() -> createCompany(request));
    }

    private Company updateCompany(
            Company company,
            CreateCreditRequestRequest request) {
        company.updateRegistrationData(
                request.companyName(),
                request.registrationStatus(),
                request.postalCode(),
                request.city(),
                request.state(),
                request.foundedAt());
        return companyRepository.save(company);
    }

    private Company createCompany(CreateCreditRequestRequest request) {
        Company company = Company.create(
                request.cnpj(),
                request.companyName(),
                request.registrationStatus(),
                request.postalCode(),
                request.city(),
                request.state(),
                request.foundedAt());
        return companyRepository.save(company);
    }
}
