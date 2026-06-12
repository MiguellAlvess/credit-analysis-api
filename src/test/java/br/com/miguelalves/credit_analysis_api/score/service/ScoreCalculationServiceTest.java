package br.com.miguelalves.credit_analysis_api.score.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.credit_analysis_api.company.domain.Company;
import br.com.miguelalves.credit_analysis_api.company.domain.RegistrationStatus;
import br.com.miguelalves.credit_analysis_api.request.domain.CreditRequest;
import br.com.miguelalves.credit_analysis_api.score.domain.RiskLevel;

@ExtendWith(MockitoExtension.class)
public class ScoreCalculationServiceTest {

        @InjectMocks
        private ScoreCalculationService scoreCalculationService;

        @Test
        void shouldCalculateScoreForActiveCompanyWithMoreThanFiveYearsAndHighRevenue() {
                Company company = Company.create(
                                "12345678000195",
                                "Empresa XPTO",
                                RegistrationStatus.ACTIVE,
                                "58400000",
                                "Campina Grande",
                                "PB",
                                LocalDate.now().minusYears(6));
                CreditRequest creditRequest = CreditRequest.create(
                                company,
                                new BigDecimal("100000.00"),
                                new BigDecimal("1500000.00"));

                int score = scoreCalculationService.calculate(company, creditRequest.getAnnualRevenue());

                assertThat(score).isEqualTo(1000);
        }

        @Test
        void shouldCalculateScoreForInactiveCompanyWithLessThanFiveYearsAndLowRevenue() {
                Company company = Company.create(
                                "12345678000195",
                                "Empresa XPTO",
                                RegistrationStatus.CLOSED,
                                "58400000",
                                "Campina Grande",
                                "PB",
                                LocalDate.now().minusYears(2));
                CreditRequest creditRequest = CreditRequest.create(
                                company,
                                new BigDecimal("100000.00"),
                                new BigDecimal("500000.00"));

                int score = scoreCalculationService.calculate(company, creditRequest.getAnnualRevenue());

                assertThat(score).isEqualTo(0);
        }

        @Test
        void shouldClassifyHighRiskWhenScoreIsBetweenZeroAnd399() {
                assertThat(scoreCalculationService.classifyRiskLevel(399))
                                .isEqualTo(RiskLevel.HIGH);
        }

        @Test
        void shouldClassifyMediumRiskWhenScoreIsBetween400And699() {
                assertThat(scoreCalculationService.classifyRiskLevel(400))
                                .isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        void shouldClassifyLowRiskWhenScoreIsBetween700And1000() {
                assertThat(scoreCalculationService.classifyRiskLevel(700))
                                .isEqualTo(RiskLevel.LOW);
        }

}
