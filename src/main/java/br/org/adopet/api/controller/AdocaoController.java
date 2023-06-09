package br.org.adopet.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.org.adopet.api.domain.dto.AdocaoCadastro;
import br.org.adopet.api.domain.dto.AdocaoDetalhamentoDTO;
import br.org.adopet.api.domain.dto.MensagemDTO;
import br.org.adopet.api.domain.model.Adocao;
import br.org.adopet.api.domain.model.Pet;
import br.org.adopet.api.domain.model.Tutor;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("adocao")
public class AdocaoController extends BaseController {

	@GetMapping
	public ResponseEntity<Page<AdocaoDetalhamentoDTO>> getListarAdocoes(
			@PageableDefault(size = 9, sort = { "data" }, direction = Direction.DESC) Pageable paginacao) {
		Page<AdocaoDetalhamentoDTO> paginaAdocoes = super.adocaoRepository().findAll(paginacao).map(AdocaoDetalhamentoDTO::new);
		if(!paginaAdocoes.hasContent()) {
			throw new EntityNotFoundException("Esta página de adoções está vazia.");
		}
		return ResponseEntity.ok(paginaAdocoes);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<AdocaoDetalhamentoDTO> postCriarAdocao(@RequestBody @Valid AdocaoCadastro dadosAdocao) {
		Pet pet = super.buscarEntidade(dadosAdocao.petId(), super.petRepository(), "pet");
		Tutor tutor = super.buscarEntidade(dadosAdocao.tutorId(), super.tutorRepository(), "tutor");
		Adocao adocao = super.adocaoRepository().save(new Adocao(pet, tutor));
		return ResponseEntity.ok(new AdocaoDetalhamentoDTO(adocao));
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<MensagemDTO> deleteCancelarAdocao(@PathVariable Long id) {
		Adocao adocao = super.buscarEntidade(id, super.adocaoRepository(), "adocao");
		adocao.cancelar();
		super.adocaoRepository().delete(adocao);
		MensagemDTO mensagemDTO = new MensagemDTO(String.format("A adoção com o ID %d foi excluído com sucesso.", id));
		return ResponseEntity.ok(mensagemDTO);
	}
}
