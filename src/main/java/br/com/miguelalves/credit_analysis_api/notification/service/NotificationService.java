package br.com.miguelalves.credit_analysis_api.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    public void notifyDecision(CreditDecision decision) {
        LOGGER.info(
                "Credit decision notification sent. RequestId: {}, Decision: {}, Reason: {}",
                decision.getCreditRequest().getId(),
                decision.getDecision(),
                decision.getReason());
    }
}
