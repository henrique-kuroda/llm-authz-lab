package com.henriqueKuroda.llm_authz_lab.consulta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExtratorSqlTest {

	@Test
	void devolveTextoSemCercaComoVeio() {
		assertThat(ExtratorSql.extrair("  SELECT 1;\n")).isEqualTo("SELECT 1;");
	}

	@Test
	void tiraCercaComLinguagem() {
		assertThat(ExtratorSql.extrair("```sql\nSELECT * FROM projeto;\n```")).isEqualTo("SELECT * FROM projeto;");
	}

	@Test
	void tiraCercaSemLinguagemComTextoEmVolta() {
		assertThat(ExtratorSql.extrair("Aqui está:\n```\nSELECT 1;\n```\nPronto.")).isEqualTo("SELECT 1;");
	}

	@Test
	void preservaMultiplosStatements() {
		assertThat(ExtratorSql.extrair("```sql\nSELECT 1; DROP TABLE salario;\n```"))
				.isEqualTo("SELECT 1; DROP TABLE salario;");
	}

	@Test
	void nuloViraVazio() {
		assertThat(ExtratorSql.extrair(null)).isEmpty();
	}
}
