package com.henriqueKuroda.llm_authz_lab.auth;

public record UsuarioAutenticado(String login, Perfil perfil, int funcionarioId, int departamentoId) {
}
