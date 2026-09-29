package com.henriqueKuroda.llm_authz_lab.llm;

public interface LlmClient {

	RespostaLlm gerar(String instrucaoSistema, String mensagemUsuario);
}
