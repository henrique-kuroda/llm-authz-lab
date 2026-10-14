package com.henriqueKuroda.llm_authz_lab.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ConsultaRequest(@NotBlank @Schema(example = "Qual é o meu salário?") String pergunta) {
}
