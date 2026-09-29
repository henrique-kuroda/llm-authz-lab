package com.henriqueKuroda.llm_authz_lab.consulta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Tira o SQL de dentro de cercas de markdown, que os modelos costumam devolver mesmo instruídos a não usar. */
public final class ExtratorSql {

	private static final Pattern CERCA = Pattern.compile("```(?:sql|postgresql)?\\s*\\n?(.*?)```",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	private ExtratorSql() {
	}

	public static String extrair(String textoLlm) {
		if (textoLlm == null) {
			return "";
		}
		Matcher m = CERCA.matcher(textoLlm);
		return (m.find() ? m.group(1) : textoLlm).strip();
	}
}
