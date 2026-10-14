package com.henriqueKuroda.llm_authz_lab.config;

import org.springframework.context.annotation.Configuration;

import com.henriqueKuroda.llm_authz_lab.auth.UsuarioAutenticadoResolver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/** O header X-Usuario vira o botão "Authorize" do Swagger UI: informe o login uma vez (joao, maria, carla...). */
@Configuration
@OpenAPIDefinition(
		info = @Info(title = "llm-authz-lab", version = "0.0.1",
				description = "Laboratório de fronteira de autorização LLM → banco. Personas em docs/matriz-autorizacao.md."),
		security = @SecurityRequirement(name = "usuario"))
@SecurityScheme(name = "usuario", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER,
		paramName = UsuarioAutenticadoResolver.HEADER,
		description = "Login simulado: joao, ana (colaborador), maria, pedro, lucia, sergio (gerente), carla (rh), admin")
public class OpenApiConfig {
}
