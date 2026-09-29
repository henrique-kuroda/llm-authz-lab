package com.henriqueKuroda.llm_authz_lab.llm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Chamada direta à API REST do Gemini (generateContent), sem SDK, para controlar modelo e temperatura. */
@Component
public class GeminiClient implements LlmClient {

	private final RestClient restClient;
	private final GeminiProperties props;

	public GeminiClient(RestClient.Builder builder, GeminiProperties props) {
		this.props = props;
		this.restClient = builder.baseUrl(props.baseUrl()).build();
	}

	@Override
	public RespostaLlm gerar(String instrucaoSistema, String mensagemUsuario) {
		if (props.apiKey() == null || props.apiKey().isBlank()) {
			throw new LlmException("GEMINI_API_KEY não configurada no .env");
		}

		Map<String, Object> corpo = Map.of(
				"systemInstruction", Map.of("parts", List.of(Map.of("text", instrucaoSistema))),
				"contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", mensagemUsuario)))),
				"generationConfig", Map.of("temperature", props.temperature()));

		GeminiResponse resposta;
		try {
			resposta = restClient.post()
					.uri("/models/{modelo}:generateContent", props.model())
					.header("x-goog-api-key", props.apiKey())
					.body(corpo)
					.retrieve()
					.body(GeminiResponse.class);
		} catch (RestClientException e) {
			throw new LlmException("Falha ao chamar o Gemini: " + e.getMessage(), e);
		}

		if (resposta == null || resposta.candidates() == null || resposta.candidates().isEmpty()) {
			throw new LlmException("Gemini não devolveu candidatos (bloqueio do provedor?)");
		}
		Candidate candidato = resposta.candidates().getFirst();
		if (candidato.content() == null || candidato.content().parts() == null) {
			throw new LlmException("Resposta vazia do Gemini (finishReason=" + candidato.finishReason() + ")");
		}
		String texto = candidato.content().parts().stream()
				.filter(p -> !Boolean.TRUE.equals(p.thought()) && p.text() != null)
				.map(Part::text)
				.collect(Collectors.joining());
		return new RespostaLlm(texto, resposta.modelVersion() != null ? resposta.modelVersion() : props.model());
	}

	record GeminiResponse(List<Candidate> candidates, String modelVersion) {
	}

	record Candidate(Content content, String finishReason) {
	}

	record Content(List<Part> parts) {
	}

	record Part(String text, Boolean thought) {
	}
}
