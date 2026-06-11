package br.com.miguelalves.credit_analysis_api.request.service;

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

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyService;
import br.com.miguelalves.credit_analysis_api.company.service.CompanyValidationService;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.COMPANY_ID;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.CREATE_CREDIT_REQUEST_REQUEST;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.CREDIT_REQUEST_ID;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.createCompany;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.createCreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.repository.CreditRequestRepository;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CreditRequestServiceTest {

        @InjectMocks
        private CreditRequestService creditRequestService;

        @Mock
        private CompanyService companyService;

        @Mock
        private CompanyValidationService companyValidationService;

        @Mock
        private CreditRequestRepository creditRequestRepository;

        @Test
        void shouldCreateCreditRequestWithValidData() {
                Company company = createCompany();
                when(companyService.findCompanyById(CREATE_CREDIT_REQUEST_REQUEST.companyId()))
                                .thenReturn(company);
                when(companyValidationService.validateCompany(company))
                                .thenReturn(company);
                when(creditRequestRepository.save(any(CreditRequest.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                CreditRequestResponse response = creditRequestService
                                .createCreditRequest(CREATE_CREDIT_REQUEST_REQUEST);

                assertThat(response).isNotNull();
                assertThat(response.companyId())
                                .isEqualTo(CREATE_CREDIT_REQUEST_REQUEST.companyId());
                assertThat(response.requestedAmount())
                                .isEqualTo(CREATE_CREDIT_REQUEST_REQUEST.requestedAmount());
                assertThat(response.annualRevenue())
                                .isEqualTo(CREATE_CREDIT_REQUEST_REQUEST.annualRevenue());
                assertThat(response.status())
                                .isEqualTo(CreditRequestStatus.PENDING);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCompanyDoesNotExist() {
                when(companyService.findCompanyById(COMPANY_ID))
                                .thenThrow(new ResourceNotFoundException("Company not found"));

                assertThatThrownBy(() -> creditRequestService.createCreditRequest(CREATE_CREDIT_REQUEST_REQUEST))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Company not found");
        }

        @Test
        void shouldGetCreditRequestByIdWhenCreditRequestExists() {
                CreditRequest creditRequest = createCreditRequest();
                when(creditRequestRepository.findById(CREDIT_REQUEST_ID))
                                .thenReturn(Optional.of(creditRequest));

                CreditRequestResponse response = creditRequestService.getCreditRequestById(CREDIT_REQUEST_ID);

                assertThat(response).isNotNull();
                assertThat(response.companyId()).isEqualTo(creditRequest.getCompany().getId());
                assertThat(response.companyName()).isEqualTo(creditRequest.getCompany().getName());
                assertThat(response.cnpj()).isEqualTo(creditRequest.getCompany().getCnpj());
                assertThat(response.requestedAmount()).isEqualByComparingTo(creditRequest.getRequestedAmount());
                assertThat(response.annualRevenue()).isEqualByComparingTo(creditRequest.getAnnualRevenue());
                assertThat(response.status()).isEqualTo(creditRequest.getStatus());
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCreditRequestDoesNotExistById() {
                when(creditRequestRepository.findById(CREDIT_REQUEST_ID))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> creditRequestService.getCreditRequestById(CREDIT_REQUEST_ID))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessage("Credit request not found");
        }
}
