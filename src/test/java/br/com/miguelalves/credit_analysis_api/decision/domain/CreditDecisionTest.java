package br.com.miguelalves.credit_analysis_api.decision.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;

public class CreditDecisionTest {
    @Test
    void shouldCreateApprovedCreditDecision() {
        CreditRequest creditRequest = createCreditRequest();

        CreditDecision creditDecision = CreditDecision.approve(
                creditRequest,
                new BigDecimal("150000.00"),
                "Credit request approved within allowed limit");

        assertThat(creditDecision.getId()).isNotNull();
        assertThat(creditDecision.getCreditRequest()).isEqualTo(creditRequest);
        assertThat(creditDecision.getDecision()).isEqualTo(CreditDecisionType.APPROVED);
        assertThat(creditDecision.getApprovedAmount()).isEqualByComparingTo("150000.00");
        assertThat(creditDecision.getReason()).isEqualTo("Credit request approved within allowed limit");
        assertThat(creditDecision.getDecidedAt()).isNotNull();
        assertThat(creditDecision.isApproved()).isTrue();
        assertThat(creditDecision.isRejected()).isFalse();
        assertThat(creditDecision.isManualReview()).isFalse();
    }

    @Test
    void shouldCreateRejectedCreditDecision() {
        CreditRequest creditRequest = createCreditRequest();

        CreditDecision creditDecision = CreditDecision.reject(
                creditRequest,
                "Company has high risk level");

        assertThat(creditDecision.getId()).isNotNull();
        assertThat(creditDecision.getCreditRequest()).isEqualTo(creditRequest);
        assertThat(creditDecision.getDecision()).isEqualTo(CreditDecisionType.REJECTED);
        assertThat(creditDecision.getApprovedAmount()).isEqualByComparingTo("0.00");
        assertThat(creditDecision.getReason()).isEqualTo("Company has high risk level");
        assertThat(creditDecision.getDecidedAt()).isNotNull();
        assertThat(creditDecision.isRejected()).isTrue();
        assertThat(creditDecision.isApproved()).isFalse();
        assertThat(creditDecision.isManualReview()).isFalse();
    }

    @Test
    void shouldCreateManualReviewCreditDecision() {
        CreditRequest creditRequest = createCreditRequest();

        CreditDecision creditDecision = CreditDecision.manualReview(
                creditRequest,
                "Company has medium risk level");

        assertThat(creditDecision.getId()).isNotNull();
        assertThat(creditDecision.getCreditRequest()).isEqualTo(creditRequest);
        assertThat(creditDecision.getDecision()).isEqualTo(CreditDecisionType.MANUAL_REVIEW);
        assertThat(creditDecision.getApprovedAmount()).isEqualByComparingTo("0.00");
        assertThat(creditDecision.getReason()).isEqualTo("Company has medium risk level");
        assertThat(creditDecision.getDecidedAt()).isNotNull();
        assertThat(creditDecision.isManualReview()).isTrue();
        assertThat(creditDecision.isApproved()).isFalse();
        assertThat(creditDecision.isRejected()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenCreditRequestIsNull() {
        assertThatThrownBy(() -> CreditDecision.approve(
                null,
                new BigDecimal("150000.00"),
                "Credit request approved within allowed limit"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credit request is required");
    }

    @Test
    void shouldThrowExceptionWhenApprovedAmountIsNegative() {
        assertThatThrownBy(() -> CreditDecision.approve(
                createCreditRequest(),
                new BigDecimal("-1000.00"),
                "Credit request approved within allowed limit"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Approved amount cannot be negative");
    }

    @Test
    void shouldThrowExceptionWhenReasonIsNull() {
        assertThatThrownBy(() -> CreditDecision.reject(
                createCreditRequest(),
                null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Decision reason is required");
    }

    @Test
    void shouldThrowExceptionWhenReasonIsBlank() {
        assertThatThrownBy(() -> CreditDecision.manualReview(
                createCreditRequest(),
                " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Decision reason is required");
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
