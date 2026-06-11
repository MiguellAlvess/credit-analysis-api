package br.com.miguelalves.credit_analysis_api.request.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.credit_analysis_api.request.dto.CreateCreditRequestRequest;
import br.com.miguelalves.credit_analysis_api.request.dto.CreditRequestResponse;
import br.com.miguelalves.credit_analysis_api.request.service.CreditRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CreditRequestController {

    private final CreditRequestService creditRequestCreationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditRequestResponse createCreditRequest(
            @RequestBody @Valid CreateCreditRequestRequest request) {
        return creditRequestCreationService.createCreditRequest(request);
    }

    @GetMapping("/{id}")
    public CreditRequestResponse getCreditRequestById(@PathVariable UUID id) {
        return creditRequestCreationService.getCreditRequestById(id);
    }
}
