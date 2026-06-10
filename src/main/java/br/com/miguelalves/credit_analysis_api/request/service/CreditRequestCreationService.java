package br.com.miguelalves.credit_analysis_api.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreateCreditRequestRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.mapper.CreditRequestMapper;
import br.com.miguelalves.credit_analysis_api.request.repository.CreditRequestRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditRequestCreationService {

    private final CompanyRepository companyRepository;
    private final CreditRequestRepository creditRequestRepository;

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
                .map(company -> updateCompanyRegistrationData(company, request))
                .orElseGet(() -> createCompany(request));
    }

    private Company updateCompanyRegistrationData(
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
