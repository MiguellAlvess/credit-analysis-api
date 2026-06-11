package br.com.miguelalves.credit_analysis_api.integration.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.miguelalves.credit_analysis_api.integration.dto.BrasilApiCompanyResponse;
import br.com.miguelalves.credit_analysis_api.integration.exception.BrasilApiException;

@Service
public class BrasilApiClient {

    private final RestClient restClient;

    public BrasilApiClient(
            RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://brasilapi.com.br/api")
                .build();
    }

    public BrasilApiCompanyResponse findCompanyByCnpj(String cnpj) {
        try {
            return restClient.get()
                    .uri("/cnpj/v1/{cnpj}", cnpj)
                    .retrieve()
                    .body(BrasilApiCompanyResponse.class);
        } catch (Exception ex) {
            throw new BrasilApiException(
                    "Error while consulting BrasilAPI",
                    ex);
        }
    }
}
