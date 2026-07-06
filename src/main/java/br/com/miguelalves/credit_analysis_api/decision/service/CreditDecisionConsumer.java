package br.com.miguelalves.credit_analysis_api.decision.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.decision.dto.CreditDecisionEvent;
import br.com.miguelalves.credit_analysis_api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditDecisionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "credit-decision-topic", groupId = "credit-analysis-notification-group")
    public void consume(CreditDecisionEvent event) {
        notificationService.notifyDecision(event);
        System.out.println("Credit decision consumed: " + event);
    }
}
