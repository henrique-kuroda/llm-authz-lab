package com.henriqueKuroda.llm_authz_lab.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.henriqueKuroda.llm_authz_lab.auth.UsuarioAutenticadoResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final UsuarioAutenticadoResolver usuarioAutenticadoResolver;

	public WebConfig(UsuarioAutenticadoResolver usuarioAutenticadoResolver) {
		this.usuarioAutenticadoResolver = usuarioAutenticadoResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(usuarioAutenticadoResolver);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/swagger-ui.html");
	}
}
