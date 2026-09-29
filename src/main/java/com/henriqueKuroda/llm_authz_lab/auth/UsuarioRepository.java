package com.henriqueKuroda.llm_authz_lab.auth;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

	private final JdbcTemplate jdbcTemplate;

	public UsuarioRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<UsuarioAutenticado> buscarPorLogin(String login) {
		return jdbcTemplate.query("""
				SELECT u.login, u.perfil, f.id, f.departamento_id
				FROM auth.usuario u
				JOIN funcionario f ON f.usuario_login = u.login
				WHERE u.login = ?
				""",
				(rs, i) -> new UsuarioAutenticado(
						rs.getString("login"),
						Perfil.doBanco(rs.getString("perfil")),
						rs.getInt("id"),
						rs.getInt("departamento_id")),
				login).stream().findFirst();
	}
}
