package br.com.miguelalves.credit_analysis_api.decision.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.credit_analysis_api.decision.dto.CreditDecisionResponse;
import br.com.miguelalves.credit_analysis_api.decision.service.CreditDecisionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class CreditDecisionController {

    private final CreditDecisionService creditDecisionService;

    @GetMapping("/request/{requestId}")
    public CreditDecisionResponse getDecisionByRequestId(
            @PathVariable UUID requestId) {
        return creditDecisionService.getDecisionByRequestId(requestId);
    }
}