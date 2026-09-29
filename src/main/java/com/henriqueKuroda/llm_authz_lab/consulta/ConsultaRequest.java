package com.henriqueKuroda.llm_authz_lab.consulta;

import jakarta.validation.constraints.NotBlank;

public record ConsultaRequest(@NotBlank String pergunta) {
}
