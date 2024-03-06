package com.medilabo.backdiabete;

import com.medilabo.backdiabete.controller.DiabeteController;
import com.medilabo.backdiabete.service.DiabetesRiskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;


@WebFluxTest(DiabeteController.class)
public class DiabeteControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private DiabetesRiskService diabetesRiskService;

	@Test
	public void getDiabetesRiskLevel_ReturnsRiskLevel() {
		// Setup
		String expectedRiskLevel = "Early onset";
		given(diabetesRiskService.determineRiskLevel(anyInt())).willReturn(Mono.just(expectedRiskLevel));

		// Execute & Assert
		webTestClient.get().uri("/diabetes/risk/1")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo(expectedRiskLevel);
	}

	@Test
	public void getDiabetesRiskLevel_ReturnsNotFound() {
		// Setup
		given(diabetesRiskService.determineRiskLevel(anyInt())).willReturn(Mono.empty());

		// Execute & Assert
		webTestClient.get().uri("/diabetes/risk/999")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound();
	}
}
