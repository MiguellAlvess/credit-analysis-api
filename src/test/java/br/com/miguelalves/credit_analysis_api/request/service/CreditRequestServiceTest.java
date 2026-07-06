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
import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.repository.CreditDecisionRepository;
import br.com.miguelalves.credit_analysis_api.decision.service.CreditDecisionService;
import br.com.miguelalves.credit_analysis_api.policy.service.CreditPolicyService;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.COMPANY_ID;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.CREATE_CREDIT_REQUEST_REQUEST;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.CREDIT_REQUEST_ID;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.createCompany;
import static br.com.miguelalves.credit_analysis_api.request.common.CreditRequestConstants.createCreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.repository.CreditRequestRepository;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;
import br.com.miguelalves.credit_analysis_api.score.service.ScoreCalculationService;
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
        private ScoreCalculationService scoreCalculationService;

        @Mock
        private CreditPolicyService creditPolicyService;

        @Mock
        private CreditDecisionService creditDecisionService;

        @Mock
        private CreditDecisionRepository creditDecisionRepository;

        @Mock
        private CreditRequestRepository creditRequestRepository;

        @Test
        void shouldCreateCreditRequestWithValidData() {
                Company company = createCompany();

                when(companyService.findCompanyById(CREATE_CREDIT_REQUEST_REQUEST.companyId()))
                                .thenReturn(company);

                when(companyValidationService.validateCompany(company))
                                .thenReturn(company);
                when(scoreCalculationService.calculate(company, CREATE_CREDIT_REQUEST_REQUEST.annualRevenue()))
                                .thenReturn(600);
                when(scoreCalculationService.classifyRiskLevel(600))
                                .thenReturn(RiskLevel.MEDIUM);
                when(creditPolicyService.evaluate(any(CreditRequest.class)))
                                .thenReturn(CreditRequestStatus.MANUAL_REVIEW);
                when(creditDecisionService.makeDecision(
                                any(CreditRequest.class),
                                any(CreditRequestStatus.class)))
                                .thenAnswer(invocation -> {
                                        CreditRequest creditRequest = invocation.getArgument(0);
                                        CreditRequestStatus status = invocation.getArgument(1);

                                        if (CreditRequestStatus.MANUAL_REVIEW.equals(status)) {
                                                creditRequest.sendToManualReview();
                                                return CreditDecision.manualReview(
                                                                creditRequest,
                                                                "Manual review required");
                                        }
                                        return null;
                                });

                when(creditRequestRepository.save(any(CreditRequest.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(creditDecisionRepository.save(any(CreditDecision.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                CreditRequestResponse response = creditRequestService
                                .createCreditRequest(CREATE_CREDIT_REQUEST_REQUEST);

                assertThat(response).isNotNull();
                assertThat(response.requestedAmount())
                                .isEqualTo(CREATE_CREDIT_REQUEST_REQUEST.requestedAmount());
                assertThat(response.annualRevenue())
                                .isEqualTo(CREATE_CREDIT_REQUEST_REQUEST.annualRevenue());
                assertThat(response.score()).isEqualTo(600);
                assertThat(response.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
                assertThat(response.status())
                                .isEqualTo(CreditRequestStatus.MANUAL_REVIEW);
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

                assertThat(response.companyId()).isEqualTo(creditRequest.getCompany().getId());
                assertThat(response.companyName()).isEqualTo(creditRequest.getCompany().getName());
                assertThat(response.cnpj()).isEqualTo(creditRequest.getCompany().getCnpj().value());
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
