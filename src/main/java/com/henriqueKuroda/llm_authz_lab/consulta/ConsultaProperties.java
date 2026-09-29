package com.henriqueKuroda.llm_authz_lab.consulta;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("consulta")
public record ConsultaProperties(int maxLinhas, int timeoutSegundos) {
}
