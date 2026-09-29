package com.henriqueKuroda.llm_authz_lab.consulta;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.henriqueKuroda.llm_authz_lab.auth.UsuarioAutenticado;

import jakarta.validation.Valid;

@RestController
public class ConsultaController {

	private final ConsultaService consultaService;

	public ConsultaController(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}

	@PostMapping("/consulta")
	public ConsultaResponse consultar(UsuarioAutenticado usuario, @Valid @RequestBody ConsultaRequest request) {
		return consultaService.consultar(usuario, request.pergunta());
	}
}
