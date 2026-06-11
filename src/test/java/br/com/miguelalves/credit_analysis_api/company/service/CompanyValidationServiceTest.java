package br.com.miguelalves.credit_analysis_api.company.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.BRASIL_API_ACTIVE_COMPANY_RESPONSE;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.BRASIL_API_CLOSED_COMPANY_RESPONSE;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.BRASIL_API_SUSPENDED_COMPANY_RESPONSE;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.createCompany;
import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.integration.client.BrasilApiClient;
import br.com.miguelalves.credit_analysis_api.integration.exception.BrasilApiException;

@ExtendWith(MockitoExtension.class)
class CompanyValidationServiceTest {

    @InjectMocks
    private CompanyValidationService companyValidationService;

    @Mock
    private BrasilApiClient brasilApiClient;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    void shouldValidateCompanyAndUpdateRegistrationDataWhenBrasilApiReturnsActiveCompany() {
        Company company = createCompany();

        when(brasilApiClient.findCompanyByCnpj(company.getCnpj()))
                .thenReturn(BRASIL_API_ACTIVE_COMPANY_RESPONSE);
        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Company validatedCompany = companyValidationService.validateCompany(company);

        assertThat(validatedCompany).isNotNull();
        assertThat(validatedCompany.getName()).isEqualTo(BRASIL_API_ACTIVE_COMPANY_RESPONSE.razao_social());
        assertThat(validatedCompany.getRegistrationStatus()).isEqualTo(RegistrationStatus.ACTIVE);
        assertThat(validatedCompany.getPostalCode()).isEqualTo(BRASIL_API_ACTIVE_COMPANY_RESPONSE.cep());
        assertThat(validatedCompany.getCity()).isEqualTo(BRASIL_API_ACTIVE_COMPANY_RESPONSE.municipio());
        assertThat(validatedCompany.getState()).isEqualTo(BRASIL_API_ACTIVE_COMPANY_RESPONSE.uf());
        assertThat(validatedCompany.getFoundedAt())
                .isEqualTo(BRASIL_API_ACTIVE_COMPANY_RESPONSE.data_inicio_atividade());
    }

    @Test
    void shouldMapSuspendedStatusWhenBrasilApiReturnsSuspendedCompany() {
        Company company = createCompany();

        when(brasilApiClient.findCompanyByCnpj(company.getCnpj()))
                .thenReturn(BRASIL_API_SUSPENDED_COMPANY_RESPONSE);
        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Company validatedCompany = companyValidationService.validateCompany(company);

        assertThat(validatedCompany.getRegistrationStatus()).isEqualTo(RegistrationStatus.SUSPENDED);
    }

    @Test
    void shouldMapClosedStatusWhenBrasilApiReturnsClosedCompany() {
        Company company = createCompany();

        when(brasilApiClient.findCompanyByCnpj(company.getCnpj()))
                .thenReturn(BRASIL_API_CLOSED_COMPANY_RESPONSE);
        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Company validatedCompany = companyValidationService.validateCompany(company);

        assertThat(validatedCompany.getRegistrationStatus()).isEqualTo(RegistrationStatus.CLOSED);
    }

    @Test
    void shouldThrowBrasilApiExceptionWhenBrasilApiClientFails() {
        Company company = createCompany();

        when(brasilApiClient.findCompanyByCnpj(company.getCnpj()))
                .thenThrow(new BrasilApiException("Error while consulting BrasilAPI", new RuntimeException()));

        assertThatThrownBy(() -> companyValidationService.validateCompany(company))
                .isInstanceOf(BrasilApiException.class)
                .hasMessage("Error while consulting BrasilAPI");
    }

}
