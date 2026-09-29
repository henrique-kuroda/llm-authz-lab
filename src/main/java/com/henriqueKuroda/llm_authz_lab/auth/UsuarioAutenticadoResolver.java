package com.henriqueKuroda.llm_authz_lab.auth;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

/**
 * Autenticação simulada do laboratório: o login vem no header {@code X-Usuario}.
 * O que importa para o experimento é que a identidade chega ao backend por um
 * canal que o LLM não controla.
 */
@Component
public class UsuarioAutenticadoResolver implements HandlerMethodArgumentResolver {

	public static final String HEADER = "X-Usuario";

	private final UsuarioRepository usuarioRepository;

	public UsuarioAutenticadoResolver(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(UsuarioAutenticado.class);
	}

	@Override
	public UsuarioAutenticado resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String login = webRequest.getHeader(HEADER);
		if (login == null || login.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header " + HEADER + " ausente");
		}
		return usuarioRepository.buscarPorLogin(login.trim())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário desconhecido"));
	}
}
