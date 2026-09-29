package com.henriqueKuroda.llm_authz_lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LlmAuthzLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmAuthzLabApplication.class, args);
	}

}
