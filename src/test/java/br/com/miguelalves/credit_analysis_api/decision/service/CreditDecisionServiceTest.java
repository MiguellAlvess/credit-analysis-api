package br.com.miguelalves.credit_analysis_api.decision.service;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecisionType;
import br.com.miguelalves.credit_analysis_api.decision.repository.CreditDecisionRepository;
import static br.com.miguelalves.credit_analysis_api.policy.common.CreditPolicyConstants.createCreditRequestWithRiskLevel;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;

@ExtendWith(MockitoExtension.class)
class CreditDecisionServiceTest {

        @InjectMocks
        private CreditDecisionService creditDecisionService;

        @Mock
        private CreditDecisionProducer creditDecisionProducer;

        @Mock
        private CreditDecisionRepository creditDecisionRepository;

        @Test
        void shouldApproveCreditRequestAndCreateApprovedDecision() {
                CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                                RiskLevel.LOW,
                                new BigDecimal("200000.00"),
                                new BigDecimal("1000000.00"));

                CreditDecision decision = creditDecisionService.makeDecision(
                                creditRequest,
                                CreditRequestStatus.APPROVED);

                assertThat(creditRequest.isApproved()).isTrue();
                assertThat(decision).isNotNull();
                assertThat(decision.getCreditRequest()).isEqualTo(creditRequest);
                assertThat(decision.getDecision()).isEqualTo(CreditDecisionType.APPROVED);
                assertThat(decision.getApprovedAmount())
                                .isEqualByComparingTo(new BigDecimal("300000.00"));
                assertThat(decision.getReason()).isEqualTo("Credit approved");
                assertThat(decision.isApproved()).isTrue();
        }

        @Test
        void shouldRejectCreditRequestAndCreateRejectedDecision() {
                CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                                RiskLevel.HIGH,
                                new BigDecimal("100000.00"),
                                new BigDecimal("1000000.00"));

                CreditDecision decision = creditDecisionService.makeDecision(
                                creditRequest,
                                CreditRequestStatus.REJECTED);

                assertThat(creditRequest.isRejected()).isTrue();
                assertThat(decision).isNotNull();
                assertThat(decision.getCreditRequest()).isEqualTo(creditRequest);
                assertThat(decision.getDecision()).isEqualTo(CreditDecisionType.REJECTED);
                assertThat(decision.getApprovedAmount())
                                .isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(decision.getReason()).isEqualTo("Credit rejected");
                assertThat(decision.isRejected()).isTrue();
        }

        @Test
        void shouldSendCreditRequestToManualReviewAndCreateManualReviewDecision() {
                CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                                RiskLevel.MEDIUM,
                                new BigDecimal("100000.00"),
                                new BigDecimal("1000000.00"));

                CreditDecision decision = creditDecisionService.makeDecision(
                                creditRequest,
                                CreditRequestStatus.MANUAL_REVIEW);

                assertThat(creditRequest.isWaitingManualReview()).isTrue();
                assertThat(decision).isNotNull();
                assertThat(decision.getCreditRequest()).isEqualTo(creditRequest);
                assertThat(decision.getDecision()).isEqualTo(CreditDecisionType.MANUAL_REVIEW);
                assertThat(decision.getApprovedAmount())
                                .isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(decision.getReason()).isEqualTo("Manual review required");
                assertThat(decision.isManualReview()).isTrue();
        }

        @Test
        void shouldThrowBusinessExceptionWhenCreditRequestStatusIsInvalid() {
                CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                                RiskLevel.LOW,
                                new BigDecimal("200000.00"),
                                new BigDecimal("1000000.00"));

                assertThatThrownBy(() -> creditDecisionService.makeDecision(
                                creditRequest,
                                CreditRequestStatus.PENDING))
                                .isInstanceOf(BusinessException.class)
                                .hasMessage("Invalid credit request status");
        }
}
