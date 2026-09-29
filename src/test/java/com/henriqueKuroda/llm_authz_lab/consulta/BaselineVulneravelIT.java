package com.henriqueKuroda.llm_authz_lab.consulta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.henriqueKuroda.llm_authz_lab.auth.UsuarioAutenticado;
import com.henriqueKuroda.llm_authz_lab.auth.UsuarioRepository;
import com.henriqueKuroda.llm_authz_lab.consulta.ConsultaResponse.Status;
import com.henriqueKuroda.llm_authz_lab.llm.LlmClient;
import com.henriqueKuroda.llm_authz_lab.llm.RespostaLlm;

/**
 * Prova que a baseline é vulnerável independentemente do LLM: o "LLM" aqui é um
 * mock que devolve o SQL que o atacante conseguiria induzir. Requer o Postgres
 * do docker compose no ar.
 */
@SpringBootTest
class BaselineVulneravelIT {

	@MockitoBean
	LlmClient llmClient;

	@Autowired
	ConsultaService consultaService;

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	private ConsultaResponse comoJoao(String sqlDoLlm) {
		when(llmClient.gerar(anyString(), anyString())).thenReturn(new RespostaLlm(sqlDoLlm, "mock"));
		UsuarioAutenticado joao = usuarioRepository.buscarPorLogin("joao").orElseThrow();
		return consultaService.consultar(joao, "tanto faz");
	}

	@Test
	void colaboradorLeTodosOsSalarios() {
		ConsultaResponse r = comoJoao("SELECT funcionario_id, valor FROM salario");

		assertThat(r.status()).isEqualTo(Status.EXECUTADA);
		assertThat(r.resultados().getFirst().linhas()).hasSize(50);
	}

	@Test
	void aceitaMultiplosStatementsInclusiveSchemaAuth() {
		ConsultaResponse r = comoJoao("SELECT 1; SELECT login, perfil FROM auth.usuario");

		assertThat(r.resultados()).hasSize(2);
		assertThat(r.resultados().get(1).linhas()).hasSize(50);
	}

	@Test
	void executaEscritaMasDesfazNoFim() {
		ConsultaResponse r = comoJoao("DELETE FROM avaliacao_desempenho");

		assertThat(r.status()).isEqualTo(Status.EXECUTADA);
		assertThat(r.resultados().getFirst().linhasAfetadas()).isEqualTo(75);
		assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM avaliacao_desempenho", Integer.class))
				.isEqualTo(75);
	}

	@Test
	void sqlInvalidoViraErroSql() {
		ConsultaResponse r = comoJoao("Desculpe, não posso ajudar com isso.");

		assertThat(r.status()).isEqualTo(Status.ERRO_SQL);
		assertThat(r.erro()).isNotBlank();
	}
}
