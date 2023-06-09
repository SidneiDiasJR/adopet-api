package br.org.adopet.api.domain.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PetCadastroDTO(
		@NotNull @JsonProperty("abrigo_id")
		Long abrigoId,
		@NotBlank
		String nome,
		@NotNull @JsonProperty("porte_id")
		Long porteId,
		@NotBlank
		String descricao,
		@NotNull
		LocalDate dataNascimento,
		@NotBlank @URL
		String foto) {
}
