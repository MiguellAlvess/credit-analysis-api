package br.com.miguelalves.credit_analysis_api.score.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;

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
}
