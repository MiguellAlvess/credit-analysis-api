package br.com.miguelalves.credit_analysis_api.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.miguelalves.credit_analysis_api.decision.dto.CreditDecisionEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${notification.recipient-email}")
    private String recipientEmail;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void notifyDecision(CreditDecisionEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Nova decisão de crédito gerada");
        message.setText(buildMessage(event));
        mailSender.send(message);
    }

    private String buildMessage(CreditDecisionEvent event) {
        return """
                Nova decisão de crédito gerada.

                Empresa: %s
                CNPJ: %s
                Decisão: %s
                Valor aprovado: %s
                Motivo: %s
                Data da decisão: %s
                """.formatted(
                event.companyName(),
                event.cnpj(),
                event.decision(),
                event.approvedAmount(),
                event.reason(),
                event.decidedAt());
    }
}