package br.com.miguelalves.credit_analysis_api.company.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.company.domain.Cnpj;
import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.dto.CompanyResponse;
import br.com.miguelalves.credit_analysis_api.company.dto.CreateCompanyRequest;
import br.com.miguelalves.credit_analysis_api.company.dto.UpdateCompanyRequest;
import br.com.miguelalves.credit_analysis_api.company.mapper.CompanyMapper;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if (companyRepository.findByCnpj(Cnpj.of(request.cnpj())).isPresent()) {
            throw new BusinessException("Company already exists with this CNPJ");
        }
        Company company = Company.create(
                request.cnpj(),
                request.name(),
                request.registrationStatus(),
                request.postalCode(),
                request.city(),
                request.state(),
                request.foundedAt());
        Company savedCompany = companyRepository.save(company);
        return CompanyMapper.fromCompanyToResponse(savedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(UUID id) {
        Company company = findCompanyById(id);
        return CompanyMapper.fromCompanyToResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = findCompanyById(id);
        company.updateRegistrationData(
                request.name(),
                request.registrationStatus(),
                request.postalCode(),
                request.city(),
                request.state(),
                request.foundedAt());
        Company updatedCompany = companyRepository.save(company);
        return CompanyMapper.fromCompanyToResponse(updatedCompany);
    }

    @Transactional(readOnly = true)
    public Company findCompanyById(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }
}