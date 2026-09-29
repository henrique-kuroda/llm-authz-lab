package com.henriqueKuroda.llm_authz_lab.auth;

public enum Perfil {
	COLABORADOR, GERENTE, RH, ADMIN;

	public static Perfil doBanco(String valor) {
		return valueOf(valor.toUpperCase());
	}

	public String noBanco() {
		return name().toLowerCase();
	}
}
