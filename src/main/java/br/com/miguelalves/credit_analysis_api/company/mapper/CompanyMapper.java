package br.com.miguelalves.credit_analysis_api.company.mapper;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.dto.CompanyResponse;

public class CompanyMapper {

    private CompanyMapper() {
    }

    public static CompanyResponse fromCompanyToResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getCnpj(),
                company.getName(),
                company.getRegistrationStatus(),
                company.getPostalCode(),
                company.getCity(),
                company.getState(),
                company.getFoundedAt(),
                company.getCreatedAt(),
                company.getUpdatedAt());
    }
}
