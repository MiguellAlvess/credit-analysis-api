package br.com.miguelalves.credit_analysis_api.score.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;
import br.com.miguelalves.credit_analysis_api.shared.exception.validation.BusinessException;

@Service
public class ScoreCalculationService {

    private static final int ACTIVE_COMPANY_POINTS = 300;
    private static final int MORE_THAN_FIVE_YEARS_POINTS = 300;
    private static final int HIGH_ANNUAL_REVENUE_POINTS = 400;
    private static final BigDecimal HIGH_ANNUAL_REVENUE_THRESHOLD = new BigDecimal("1000000.00");

    public int calculate(Company company, CreditRequest creditRequest) {
        int score = 0;
        if (company.isActive()) {
            score += ACTIVE_COMPANY_POINTS;
        }
        if (company.hasMoreThanFiveYearsOfOperation()) {
            score += MORE_THAN_FIVE_YEARS_POINTS;
        }
        if (creditRequest.getAnnualRevenue().compareTo(HIGH_ANNUAL_REVENUE_THRESHOLD) > 0) {
            score += HIGH_ANNUAL_REVENUE_POINTS;
        }
        return score;
    }

    public RiskLevel classifyRiskLevel(int score) {
        if (score < 0 || score > 1000) {
            throw new BusinessException("Score must be between 0 and 1000");
        }
        if (score <= 399) {
            return RiskLevel.HIGH;
        }
        if (score <= 699) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }
}
