package com.medilabo.backdiabete.configuration;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient patientWebClient(WebClient.Builder builder) {
		return builder.baseUrl("http://localhost:8082").build();
	}

	@Bean
	public WebClient notesWebClient(WebClient.Builder builder) {
		return builder.baseUrl("http://localhost:8083").build();
	}
}

