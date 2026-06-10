package br.com.miguelalves.credit_analysis_api.request.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "credit_requests")
public class CreditRequest {

    private static final BigDecimal CREDIT_LIMIT_PERCENTAGE = new BigDecimal("0.30");

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "annual_revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Column(name = "approved_limit", precision = 15, scale = 2)
    private BigDecimal approvedLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CreditRequest() {
    }

    private CreditRequest(
            Company company,
            BigDecimal requestedAmount,
            BigDecimal annualRevenue) {
        this.id = UUID.randomUUID();
        this.company = validateCompany(company);
        this.requestedAmount = validatePositiveAmount(requestedAmount, "Requested amount must be greater than zero");
        this.annualRevenue = validatePositiveAmount(annualRevenue, "Annual revenue must be greater than zero");
        this.status = CreditRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static CreditRequest create(
            Company company,
            BigDecimal requestedAmount,
            BigDecimal annualRevenue) {
        return new CreditRequest(company, requestedAmount, annualRevenue);
    }

    public void registerScore(Integer score, RiskLevel riskLevel) {
        if (score == null) {
            throw new IllegalArgumentException("Score is required");
        }
        if (score < 0 || score > 1000) {
            throw new IllegalArgumentException("Score must be between 0 and 1000");
        }
        if (riskLevel == null) {
            throw new IllegalArgumentException("Risk level is required");
        }
        this.score = score;
        this.riskLevel = riskLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal calculateMaximumAllowedAmount() {
        return this.annualRevenue
                .multiply(CREDIT_LIMIT_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean exceedsMaximumAllowedAmount() {
        return this.requestedAmount.compareTo(calculateMaximumAllowedAmount()) > 0;
    }

    public void approve() {
        this.status = CreditRequestStatus.APPROVED;
        this.approvedLimit = calculateMaximumAllowedAmount();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = CreditRequestStatus.REJECTED;
        this.approvedLimit = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    public void sendToManualReview() {
        this.status = CreditRequestStatus.MANUAL_REVIEW;
        this.approvedLimit = calculateMaximumAllowedAmount();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return CreditRequestStatus.PENDING.equals(this.status);
    }

    public boolean isApproved() {
        return CreditRequestStatus.APPROVED.equals(this.status);
    }

    public boolean isRejected() {
        return CreditRequestStatus.REJECTED.equals(this.status);
    }

    public boolean isWaitingManualReview() {
        return CreditRequestStatus.MANUAL_REVIEW.equals(this.status);
    }

    private Company validateCompany(Company company) {
        if (company == null) {
            throw new IllegalArgumentException("Company is required");
        }
        return company;
    }

    private BigDecimal validatePositiveAmount(BigDecimal amount, String message) {
        if (amount == null) {
            throw new IllegalArgumentException(message);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
