package br.com.miguelalves.credit_analysis_api.request.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

public class CreditRequestTest {
    @Test
    void shouldCreateCreditRequestWithValidData() {
        Company company = createActiveCompany();

        CreditRequest creditRequest = CreditRequest.create(
                company,
                new BigDecimal("100000.00"),
                new BigDecimal("500000.00"));

        assertThat(creditRequest.getId()).isNotNull();
        assertThat(creditRequest.getCompany()).isEqualTo(company);
        assertThat(creditRequest.getRequestedAmount()).isEqualByComparingTo("100000.00");
        assertThat(creditRequest.getAnnualRevenue()).isEqualByComparingTo("500000.00");
        assertThat(creditRequest.getStatus()).isEqualTo(CreditRequestStatus.PENDING);
        assertThat(creditRequest.getScore()).isNull();
        assertThat(creditRequest.getRiskLevel()).isNull();
        assertThat(creditRequest.getApprovedLimit()).isNull();
        assertThat(creditRequest.getCreatedAt()).isNotNull();
        assertThat(creditRequest.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenCompanyIsNull() {
        assertThatThrownBy(() -> CreditRequest.create(
                null,
                new BigDecimal("100000.00"),
                new BigDecimal("500000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Company is required");
    }

    @Test
    void shouldThrowExceptionWhenRequestedAmountIsNull() {
        assertThatThrownBy(() -> CreditRequest.create(
                createActiveCompany(),
                null,
                new BigDecimal("500000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Requested amount must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenRequestedAmountIsZero() {
        assertThatThrownBy(() -> CreditRequest.create(
                createActiveCompany(),
                BigDecimal.ZERO,
                new BigDecimal("500000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Requested amount must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenRequestedAmountIsNegative() {
        assertThatThrownBy(() -> CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("-1000.00"),
                new BigDecimal("500000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Requested amount must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenAnnualRevenueIsNull() {
        assertThatThrownBy(() -> CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("100000.00"),
                null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Annual revenue must be greater than zero");
    }

    @Test
    void shouldThrowExceptionWhenAnnualRevenueIsZero() {
        assertThatThrownBy(() -> CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("100000.00"),
                BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Annual revenue must be greater than zero");
    }

    @Test
    void shouldRegisterScoreAndRiskLevel() {
        CreditRequest creditRequest = createCreditRequest();

        creditRequest.registerScore(850, RiskLevel.LOW);

        assertThat(creditRequest.getScore()).isEqualTo(850);
        assertThat(creditRequest.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(creditRequest.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenScoreIsNull() {
        CreditRequest creditRequest = createCreditRequest();

        assertThatThrownBy(() -> creditRequest.registerScore(null, RiskLevel.LOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Score is required");
    }

    @Test
    void shouldThrowExceptionWhenScoreIsLowerThanZero() {
        CreditRequest creditRequest = createCreditRequest();

        assertThatThrownBy(() -> creditRequest.registerScore(-1, RiskLevel.LOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Score must be between 0 and 1000");
    }

    @Test
    void shouldThrowExceptionWhenScoreIsGreaterThanOneThousand() {
        CreditRequest creditRequest = createCreditRequest();

        assertThatThrownBy(() -> creditRequest.registerScore(1001, RiskLevel.LOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Score must be between 0 and 1000");
    }

    @Test
    void shouldThrowExceptionWhenRiskLevelIsNull() {
        CreditRequest creditRequest = createCreditRequest();

        assertThatThrownBy(() -> creditRequest.registerScore(850, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Risk level is required");
    }

    @Test
    void shouldCalculateMaximumAllowedAmountBasedOnThirtyPercentOfAnnualRevenue() {
        CreditRequest creditRequest = CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("100000.00"),
                new BigDecimal("1000000.00"));

        BigDecimal maximumAllowedAmount = creditRequest.calculateMaximumAllowedAmount();

        assertThat(maximumAllowedAmount).isEqualByComparingTo("300000.00");
    }

    @Test
    void shouldReturnTrueWhenRequestedAmountExceedsMaximumAllowedAmount() {
        CreditRequest creditRequest = CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("350000.00"),
                new BigDecimal("1000000.00"));

        assertThat(creditRequest.exceedsMaximumAllowedAmount()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenRequestedAmountDoesNotExceedMaximumAllowedAmount() {
        CreditRequest creditRequest = CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("200000.00"),
                new BigDecimal("1000000.00"));

        assertThat(creditRequest.exceedsMaximumAllowedAmount()).isFalse();
    }

    @Test
    void shouldApproveCreditRequest() {
        CreditRequest creditRequest = createCreditRequest();

        creditRequest.approve();

        assertThat(creditRequest.getStatus()).isEqualTo(CreditRequestStatus.APPROVED);
        assertThat(creditRequest.isApproved()).isTrue();
        assertThat(creditRequest.getApprovedLimit()).isEqualByComparingTo("150000.00");
    }

    @Test
    void shouldRejectCreditRequest() {
        CreditRequest creditRequest = createCreditRequest();

        creditRequest.reject();

        assertThat(creditRequest.getStatus()).isEqualTo(CreditRequestStatus.REJECTED);
        assertThat(creditRequest.isRejected()).isTrue();
        assertThat(creditRequest.getApprovedLimit()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldSendCreditRequestToManualReview() {
        CreditRequest creditRequest = createCreditRequest();

        creditRequest.sendToManualReview();

        assertThat(creditRequest.getStatus()).isEqualTo(CreditRequestStatus.MANUAL_REVIEW);
        assertThat(creditRequest.isWaitingManualReview()).isTrue();
        assertThat(creditRequest.getApprovedLimit()).isEqualByComparingTo("150000.00");
    }

    @Test
    void shouldReturnTrueWhenCreditRequestIsPending() {
        CreditRequest creditRequest = createCreditRequest();

        assertThat(creditRequest.isPending()).isTrue();
    }

    private CreditRequest createCreditRequest() {
        return CreditRequest.create(
                createActiveCompany(),
                new BigDecimal("100000.00"),
                new BigDecimal("500000.00"));
    }

    private Company createActiveCompany() {
        return Company.create(
                "12345678000195",
                "Empresa XPTO",
                RegistrationStatus.ACTIVE,
                "58400000",
                "Campina Grande",
                "PB",
                LocalDate.now().minusYears(3));
    }
}
