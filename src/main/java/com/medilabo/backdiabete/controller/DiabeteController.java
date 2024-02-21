package com.medilabo.backdiabete.controller;

import com.medilabo.backdiabete.service.DiabetesRiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/diabetes")
public class DiabeteController {

	private final DiabetesRiskService diabetesRiskService;

	public DiabeteController(DiabetesRiskService diabetesRiskService) {
		this.diabetesRiskService = diabetesRiskService;
	}

	@GetMapping("/risk/{patientId}")
	public Mono<ResponseEntity<String>> getDiabetesRiskLevel(@PathVariable Long patientId) {
		return diabetesRiskService.determineRiskLevel(patientId)
				.map(riskLevel -> ResponseEntity.ok().body(riskLevel))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
}

