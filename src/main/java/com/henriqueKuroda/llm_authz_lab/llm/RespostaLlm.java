package com.henriqueKuroda.llm_authz_lab.llm;

/** @param modelo versão exata do modelo que respondeu, para o relatório de execução */
public record RespostaLlm(String texto, String modelo) {
}
