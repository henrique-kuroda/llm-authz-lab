package com.henriqueKuroda.llm_authz_lab.consulta;

import java.util.List;

public record ConsultaResponse(
		Status status,
		String sql,
		List<ResultadoSql> resultados,
		String erro,
		String modelo,
		Tempos tempos) {

	public enum Status {
		EXECUTADA, ERRO_LLM, ERRO_SQL
	}

	/** Latência separada por etapa, para isolar o custo de cada camada na fase 5. */
	public record Tempos(long llmMs, long bancoMs, long totalMs) {
	}
}
