package com.medilabo.backdiabete.service;

import com.medilabo.backdiabete.model.Note;
import com.medilabo.backdiabete.model.Patient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiabetesRiskService {

	private final WebClient patientWebClient;
	private final WebClient notesWebClient;

	public DiabetesRiskService(@Qualifier("patientWebClient") WebClient patientWebClient,
	                           @Qualifier("notesWebClient") WebClient notesWebClient) {
		this.patientWebClient = patientWebClient;
		this.notesWebClient = notesWebClient;
	}

	public Mono<String> determineRiskLevel(int patientId) {
		Mono<Patient> patientMono = this.patientWebClient.get()
				.uri("/patients/{id}", patientId)
				.retrieve()
				.bodyToMono(Patient.class);

		Flux<Note> notesFlux = this.notesWebClient.get()
				.uri("/notes/{patId}", patientId)
				.retrieve()
				.bodyToFlux(Note.class);

		return patientMono.zipWith(notesFlux.collectList(), this::calculateRisk);
	}

	private String calculateRisk(Patient patient, List<Note> notes) {
		long age = ChronoUnit.YEARS.between(patient.getDateOfBirth(), LocalDate.now());
		long triggerCount = countTriggerWords(notes);

		// Aucun risque
		if (triggerCount == 0) {
			return "None";
		}

		// Apparition précoce
		if ((patient.getGender().equals("M") && age < 30 && triggerCount >= 5) ||
				(patient.getGender().equals("F") && age < 30 && triggerCount >= 7) ||
				(age > 30 && triggerCount >= 8)) {
			return "Early onset";
		}

		// Danger
		if ((patient.getGender().equals("M") && age < 30 && triggerCount >= 3) ||
				(patient.getGender().equals("F") && age < 30 && triggerCount >= 4) ||
				(age > 30 && triggerCount >= 6)) {
			return "In Danger";
		}

		// Risque limité
		if (age > 30 && triggerCount >= 2 && triggerCount <= 5) {
			return "Borderline";
		}

		// Par défaut
		return "None";
	}

	private long countTriggerWords(List<Note> notes) {
		// Définition des mots déclencheurs en minuscules
		Set<String> triggerWords = Set.of(
				"hémoglobine a1c", "microalbumine", "taille", "poids", "fumeur", "fumeuse",
				"anormal", "cholestérol", "vertiges","vertige", "rechute", "réaction", "anticorps"
		);

		// Concatène et compte
		String allNotesContent = notes.stream()
				.map(Note::getNote)
				.collect(Collectors.joining(" "))
				.toLowerCase();

		return triggerWords.stream()
				.filter(word -> allNotesContent.contains(word))
				.count();
	}

}

