package com.henriqueKuroda.llm_authz_lab.consulta;

import java.util.List;

/**
 * Um resultado por statement executado. Leitura preenche colunas/linhas;
 * escrita/DDL preenche só linhasAfetadas.
 */
public record ResultadoSql(List<String> colunas, List<List<Object>> linhas, Integer linhasAfetadas) {

	static ResultadoSql leitura(List<String> colunas, List<List<Object>> linhas) {
		return new ResultadoSql(colunas, linhas, null);
	}

	static ResultadoSql escrita(int linhasAfetadas) {
		return new ResultadoSql(null, null, linhasAfetadas);
	}
}
