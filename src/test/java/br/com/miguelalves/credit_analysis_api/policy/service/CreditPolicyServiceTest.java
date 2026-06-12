package br.com.miguelalves.credit_analysis_api.policy.service;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.miguelalves.credit_analysis_api.policy.common.CreditPolicyConstants.createCreditRequestWithRiskLevel;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequestStatus;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

@ExtendWith(MockitoExtension.class)
class CreditPolicyServiceTest {

    @InjectMocks
    private CreditPolicyService creditPolicyService;

    @Test
    void shouldRejectWhenRiskLevelIsHigh() {
        CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                RiskLevel.HIGH,
                new BigDecimal("100000.00"),
                new BigDecimal("1000000.00"));

        CreditRequestStatus status = creditPolicyService.evaluate(creditRequest);

        assertThat(status).isEqualTo(CreditRequestStatus.REJECTED);
    }

    @Test
    void shouldSendToManualReviewWhenRiskLevelIsMedium() {
        CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                RiskLevel.MEDIUM,
                new BigDecimal("100000.00"),
                new BigDecimal("1000000.00"));

        CreditRequestStatus status = creditPolicyService.evaluate(creditRequest);

        assertThat(status).isEqualTo(CreditRequestStatus.MANUAL_REVIEW);
    }

    @Test
    void shouldRejectWhenRiskLevelIsLowButRequestedAmountExceedsMaximumAllowedAmount() {
        CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                RiskLevel.LOW,
                new BigDecimal("400000.00"),
                new BigDecimal("1000000.00"));

        CreditRequestStatus status = creditPolicyService.evaluate(creditRequest);

        assertThat(status).isEqualTo(CreditRequestStatus.REJECTED);
    }

    @Test
    void shouldApproveWhenRiskLevelIsLowAndRequestedAmountIsWithinMaximumAllowedAmount() {
        CreditRequest creditRequest = createCreditRequestWithRiskLevel(
                RiskLevel.LOW,
                new BigDecimal("200000.00"),
                new BigDecimal("1000000.00"));

        CreditRequestStatus status = creditPolicyService.evaluate(creditRequest);

        assertThat(status).isEqualTo(CreditRequestStatus.APPROVED);
    }
}
