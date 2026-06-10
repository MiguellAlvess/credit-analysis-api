package br.com.miguelalves.credit_analysis_api.decision.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "credit_decisions")
public class CreditDecision {

    @Id
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_request_id", nullable = false, unique = true)
    private CreditRequest creditRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditDecisionType decision;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;

    protected CreditDecision() {
    }

    private CreditDecision(
            CreditRequest creditRequest,
            CreditDecisionType decision,
            BigDecimal approvedAmount,
            String reason) {
        this.id = UUID.randomUUID();
        this.creditRequest = validateCreditRequest(creditRequest);
        this.decision = validateDecision(decision);
        this.approvedAmount = normalizeApprovedAmount(approvedAmount);
        this.reason = validateReason(reason);
        this.decidedAt = LocalDateTime.now();
    }

    public static CreditDecision approve(
            CreditRequest creditRequest,
            BigDecimal approvedAmount,
            String reason) {
        return new CreditDecision(
                creditRequest,
                CreditDecisionType.APPROVED,
                approvedAmount,
                reason);
    }

    public static CreditDecision reject(
            CreditRequest creditRequest,
            String reason) {
        return new CreditDecision(
                creditRequest,
                CreditDecisionType.REJECTED,
                BigDecimal.ZERO,
                reason);
    }

    public static CreditDecision manualReview(
            CreditRequest creditRequest,
            String reason) {
        return new CreditDecision(
                creditRequest,
                CreditDecisionType.MANUAL_REVIEW,
                BigDecimal.ZERO,
                reason);
    }

    public boolean isApproved() {
        return CreditDecisionType.APPROVED.equals(this.decision);
    }

    public boolean isRejected() {
        return CreditDecisionType.REJECTED.equals(this.decision);
    }

    public boolean isManualReview() {
        return CreditDecisionType.MANUAL_REVIEW.equals(this.decision);
    }

    private CreditRequest validateCreditRequest(CreditRequest creditRequest) {
        if (creditRequest == null) {
            throw new IllegalArgumentException("Credit request is required");
        }
        return creditRequest;
    }

    private CreditDecisionType validateDecision(CreditDecisionType decision) {
        if (decision == null) {
            throw new IllegalArgumentException("Credit decision is required");
        }
        return decision;
    }

    private BigDecimal normalizeApprovedAmount(BigDecimal approvedAmount) {
        if (approvedAmount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (approvedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Approved amount cannot be negative");
        }
        return approvedAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private String validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Decision reason is required");
        }
        return reason;
    }
}
