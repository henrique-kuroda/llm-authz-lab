package com.henriqueKuroda.llm_authz_lab.consulta;

import java.util.List;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.henriqueKuroda.llm_authz_lab.auth.UsuarioAutenticado;
import com.henriqueKuroda.llm_authz_lab.consulta.ConsultaResponse.Status;
import com.henriqueKuroda.llm_authz_lab.consulta.ConsultaResponse.Tempos;
import com.henriqueKuroda.llm_authz_lab.llm.LlmClient;
import com.henriqueKuroda.llm_authz_lab.llm.LlmException;
import com.henriqueKuroda.llm_authz_lab.llm.RespostaLlm;

/**
 * Pipeline baseline: pergunta → LLM → SQL cru → banco → linhas.
 * Nenhuma verificação de autorização, de propósito.
 */
@Service
public class ConsultaService {

	private final LlmClient llmClient;
	private final PromptBaseline prompt;
	private final ExecutorSql executor;

	public ConsultaService(LlmClient llmClient, PromptBaseline prompt, ExecutorSql executor) {
		this.llmClient = llmClient;
		this.prompt = prompt;
		this.executor = executor;
	}

	public ConsultaResponse consultar(UsuarioAutenticado usuario, String pergunta) {
		long inicio = System.nanoTime();

		RespostaLlm resposta;
		try {
			resposta = llmClient.gerar(prompt.instrucaoSistema(usuario), pergunta);
		} catch (LlmException e) {
			long llm = ms(inicio, System.nanoTime());
			return new ConsultaResponse(Status.ERRO_LLM, null, List.of(), e.getMessage(), null,
					new Tempos(llm, 0, llm));
		}
		long fimLlm = System.nanoTime();
		String sql = ExtratorSql.extrair(resposta.texto());

		try {
			List<ResultadoSql> resultados = executor.executar(sql);
			return new ConsultaResponse(Status.EXECUTADA, sql, resultados, null, resposta.modelo(),
					tempos(inicio, fimLlm));
		} catch (DataAccessException e) {
			return new ConsultaResponse(Status.ERRO_SQL, sql, List.of(),
					NestedExceptionUtils.getMostSpecificCause(e).getMessage(), resposta.modelo(),
					tempos(inicio, fimLlm));
		}
	}

	private static Tempos tempos(long inicio, long fimLlm) {
		long fim = System.nanoTime();
		return new Tempos(ms(inicio, fimLlm), ms(fimLlm, fim), ms(inicio, fim));
	}

	private static long ms(long de, long ate) {
		return (ate - de) / 1_000_000;
	}
}
