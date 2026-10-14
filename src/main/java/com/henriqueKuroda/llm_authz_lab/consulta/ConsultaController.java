package com.henriqueKuroda.llm_authz_lab.consulta;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.henriqueKuroda.llm_authz_lab.auth.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
public class ConsultaController {

	private final ConsultaService consultaService;

	public ConsultaController(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}

	@Operation(summary = "Traduz a pergunta para SQL com o LLM e executa no banco",
			description = "O usuário vem do header X-Usuario (botão Authorize).")
	@PostMapping("/consulta")
	public ConsultaResponse consultar(@Parameter(hidden = true) UsuarioAutenticado usuario,
			@Valid @RequestBody ConsultaRequest request) {
		return consultaService.consultar(usuario, request.pergunta());
	}
}
