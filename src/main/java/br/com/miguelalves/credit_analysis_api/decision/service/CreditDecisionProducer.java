package br.com.miguelalves.credit_analysis_api.decision.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.decision.domain.CreditDecision;
import br.com.miguelalves.credit_analysis_api.decision.dto.CreditDecisionEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditDecisionProducer {

    private static final String TOPIC = "credit-decision-topic";

    private final KafkaTemplate<String, CreditDecisionEvent> kafkaTemplate;

    public void publish(CreditDecision decision) {
        CreditDecisionEvent event = new CreditDecisionEvent(
                decision.getCreditRequest().getId(),
                decision.getCreditRequest().getCompany().getName(),
                decision.getCreditRequest().getCompany().getCnpj(),
                decision.getDecision(),
                decision.getApprovedAmount(),
                decision.getReason(),
                decision.getDecidedAt());
        kafkaTemplate.send(
                TOPIC,
                decision.getCreditRequest().getId().toString(),
                event);
        System.out.println("Credit decision published: " + event);
    }
}
