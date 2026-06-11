package br.com.miguelalves.credit_analysis_api.integration.dto;

import java.time.LocalDate;

public record BrasilApiCompanyResponse(
                String cnpj,
                String razao_social,
                String cep,
                String municipio,
                String uf,
                String descricao_situacao_cadastral,
                LocalDate data_inicio_atividade) {
}
