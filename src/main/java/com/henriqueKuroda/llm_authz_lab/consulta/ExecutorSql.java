package com.henriqueKuroda.llm_authz_lab.consulta;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Executor ingênuo: roda o texto do LLM como veio, com o superusuário do banco,
 * aceitando múltiplos statements e qualquer tipo de comando. Vulnerável de propósito.
 *
 * Tudo roda numa transação que sempre sofre rollback — isso NÃO é defesa (um
 * COMMIT dentro do texto fura), serve só para o laboratório não se degradar
 * entre execuções. Se o banco for alterado, recrie com docker compose down -v.
 */
@Component
public class ExecutorSql {

	private final JdbcTemplate jdbcTemplate;
	private final ConsultaProperties props;

	public ExecutorSql(JdbcTemplate jdbcTemplate, ConsultaProperties props) {
		this.jdbcTemplate = jdbcTemplate;
		this.props = props;
	}

	public List<ResultadoSql> executar(String sql) {
		return jdbcTemplate.execute((ConnectionCallback<List<ResultadoSql>>) con -> {
			boolean autoCommitOriginal = con.getAutoCommit();
			con.setAutoCommit(false);
			try (Statement stmt = con.createStatement()) {
				stmt.setMaxRows(props.maxLinhas());
				stmt.setQueryTimeout(props.timeoutSegundos());

				List<ResultadoSql> resultados = new ArrayList<>();
				boolean temResultSet = stmt.execute(sql);
				while (true) {
					if (temResultSet) {
						try (ResultSet rs = stmt.getResultSet()) {
							resultados.add(ler(rs));
						}
					} else {
						int afetadas = stmt.getUpdateCount();
						if (afetadas == -1) {
							break;
						}
						resultados.add(ResultadoSql.escrita(afetadas));
					}
					temResultSet = stmt.getMoreResults();
				}
				return resultados;
			} finally {
				con.rollback();
				con.setAutoCommit(autoCommitOriginal);
			}
		});
	}

	private static ResultadoSql ler(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int n = meta.getColumnCount();
		List<String> colunas = new ArrayList<>(n);
		for (int i = 1; i <= n; i++) {
			colunas.add(meta.getColumnLabel(i));
		}
		List<List<Object>> linhas = new ArrayList<>();
		while (rs.next()) {
			List<Object> linha = new ArrayList<>(n);
			for (int i = 1; i <= n; i++) {
				linha.add(valorSerializavel(rs.getObject(i)));
			}
			linhas.add(linha);
		}
		return ResultadoSql.leitura(colunas, linhas);
	}

	private static Object valorSerializavel(Object valor) {
		if (valor == null || valor instanceof Number || valor instanceof String || valor instanceof Boolean) {
			return valor;
		}
		return valor.toString();
	}
}
