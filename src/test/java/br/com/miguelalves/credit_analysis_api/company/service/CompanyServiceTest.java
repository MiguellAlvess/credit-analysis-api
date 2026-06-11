package br.com.miguelalves.credit_analysis_api.company.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.COMPANY_ID;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.CREATE_COMPANY_REQUEST;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.UPDATE_COMPANY_REQUEST;
import static br.com.miguelalves.credit_analysis_api.company.common.CompanyConstants.createCompany;
import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.dto.CompanyResponse;
import br.com.miguelalves.credit_analysis_api.company.repository.CompanyRepository;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    void shouldCreateCompanyWithValidData() {
        Company company = createCompany();
        when(companyRepository.findByCnpj(CREATE_COMPANY_REQUEST.cnpj()))
                .thenReturn(Optional.empty());

        when(companyRepository.save(any(Company.class)))
                .thenReturn(company);

        CompanyResponse response = companyService.createCompany(CREATE_COMPANY_REQUEST);

        assertThat(response).isNotNull();
        assertThat(response.cnpj()).isEqualTo(CREATE_COMPANY_REQUEST.cnpj());
        assertThat(response.name()).isEqualTo(CREATE_COMPANY_REQUEST.name());
        assertThat(response.registrationStatus()).isEqualTo(CREATE_COMPANY_REQUEST.registrationStatus());
        assertThat(response.postalCode()).isEqualTo(CREATE_COMPANY_REQUEST.postalCode());
    }

    @Test
    void shouldThrowBusinessExceptionWhenCompanyAlreadyExistsWithCnpj() {
        when(companyRepository.findByCnpj(CREATE_COMPANY_REQUEST.cnpj()))
                .thenReturn(Optional.of(createCompany()));

        assertThatThrownBy(() -> companyService.createCompany(CREATE_COMPANY_REQUEST))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Company already exists with this CNPJ");
    }

    @Test
    void shouldGetCompanyByIdWhenCompanyExists() {
        when(companyRepository.findById(COMPANY_ID))
                .thenReturn(Optional.of(createCompany()));

        CompanyResponse response = companyService.getCompanyById(COMPANY_ID);

        assertThat(response).isNotNull();
        assertThat(response.cnpj()).isEqualTo(createCompany().getCnpj());
        assertThat(response.name()).isEqualTo(createCompany().getName());
        assertThat(response.registrationStatus()).isEqualTo(createCompany().getRegistrationStatus());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCompanyDoesNotExistById() {
        when(companyRepository.findById(COMPANY_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getCompanyById(COMPANY_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Company not found");

    }

    @Test
    void shouldUpdateCompanyWithValidData() {
        when(companyRepository.findById(COMPANY_ID))
                .thenReturn(Optional.of(createCompany()));

        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CompanyResponse response = companyService.updateCompany(COMPANY_ID, UPDATE_COMPANY_REQUEST);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(UPDATE_COMPANY_REQUEST.name());
        assertThat(response.registrationStatus()).isEqualTo(UPDATE_COMPANY_REQUEST.registrationStatus());
        assertThat(response.postalCode()).isEqualTo(UPDATE_COMPANY_REQUEST.postalCode());
        assertThat(response.city()).isEqualTo(UPDATE_COMPANY_REQUEST.city());
        assertThat(response.state()).isEqualTo(UPDATE_COMPANY_REQUEST.state());
        assertThat(response.foundedAt()).isEqualTo(UPDATE_COMPANY_REQUEST.foundedAt());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistingCompany() {
        when(companyRepository.findById(COMPANY_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.updateCompany(COMPANY_ID, UPDATE_COMPANY_REQUEST))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Company not found");
    }
}
