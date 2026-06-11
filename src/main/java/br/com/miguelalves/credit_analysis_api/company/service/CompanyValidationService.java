package br.com.miguelalves.credit_analysis_api.company.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.integration.client.BrasilApiClient;
import br.com.miguelalves.credit_analysis_api.integration.dto.BrasilApiCompanyResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyValidationService {

    private final BrasilApiClient brasilApiClient;
    private final CompanyRepository companyRepository;

    @Transactional
    public Company validateCompany(Company company) {
        BrasilApiCompanyResponse response = brasilApiClient.findCompanyByCnpj(company.getCnpj());
        RegistrationStatus registrationStatus = RegistrationStatus.fromBrasilApi(
                response.descricao_situacao_cadastral());
        company.updateRegistrationData(
                response.razao_social(),
                registrationStatus,
                response.cep(),
                response.municipio(),
                response.uf(),
                response.data_inicio_atividade());
        return companyRepository.save(company);
    }
}
