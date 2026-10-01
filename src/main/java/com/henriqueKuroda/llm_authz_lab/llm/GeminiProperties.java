package com.henriqueKuroda.llm_authz_lab.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("llm.gemini")
public record GeminiProperties(String baseUrl, String apiKey, String model, double temperature, int timeoutSegundos) {
}
