package br.org.adopet.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.org.adopet.api.domain.dto.MensagemDTO;
import br.org.adopet.api.domain.dto.TutorAlteracaoDTO;
import br.org.adopet.api.domain.dto.TutorCadastroDTO;
import br.org.adopet.api.domain.dto.TutorDetalhamentoDTO;
import br.org.adopet.api.domain.model.Cidade;
import br.org.adopet.api.domain.model.Tutor;
import br.org.adopet.api.domain.repository.CidadeRepository;
import br.org.adopet.api.domain.repository.TutorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("tutores")
public class TutorController {

	@Autowired
	private TutorRepository tutorRepository;

	@Autowired
	private CidadeRepository cidadeRepository;

	@GetMapping
	public ResponseEntity<List<TutorDetalhamentoDTO>> get() {
		List<TutorDetalhamentoDTO> tutores = tutorRepository.findAll().stream().map(TutorDetalhamentoDTO::new).toList();
		if (tutores.size() == 0) {
			throw new EntityNotFoundException();
		}
		return ResponseEntity.ok(tutores);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TutorDetalhamentoDTO> get(@PathVariable Long id) {
		Tutor tutor = buscarTutorPeloId(id);
		return ResponseEntity.ok(new TutorDetalhamentoDTO(tutor));
	}

	@PostMapping
	@Transactional
	public ResponseEntity<TutorDetalhamentoDTO> post(@RequestBody @Valid TutorCadastroDTO dadosTutor) {
		Tutor tutorCriado = tutorRepository.save(new Tutor(dadosTutor));
		return ResponseEntity.ok(new TutorDetalhamentoDTO(tutorCriado));
	}

	@PutMapping
	@Transactional
	public ResponseEntity<TutorDetalhamentoDTO> put(@RequestBody @Valid TutorAlteracaoDTO dadosTutor) {
		return atualizarTutor(dadosTutor);
	}

	@PatchMapping
	@Transactional
	public ResponseEntity<TutorDetalhamentoDTO> patch(@RequestBody @Valid TutorAlteracaoDTO dadosTutor) {
		return atualizarTutor(dadosTutor);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<MensagemDTO> delete(@PathVariable Long id) {
		Tutor tutor = buscarTutorPeloId(id);
		tutorRepository.delete(tutor);
		MensagemDTO mensagemDTO = new MensagemDTO(String.format("O Tutor com ID %d foi excluído com sucesso.", id));
		return ResponseEntity.ok(mensagemDTO);
	}

	private ResponseEntity<TutorDetalhamentoDTO> atualizarTutor(TutorAlteracaoDTO dadosTutor) {
		Tutor tutor = buscarTutorPeloId(dadosTutor.id());
		Cidade cidade = buscarCidadePeloId(dadosTutor.cidadeId());
		tutor.atualizarInformações(dadosTutor, cidade);
		return ResponseEntity.ok(new TutorDetalhamentoDTO(tutor));
	}

	private Tutor buscarTutorPeloId(Long id) {
		return tutorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
				String.format("O tutor com ID %d não foi encontrado no banco de dados.", id)));
	}

	private Cidade buscarCidadePeloId(Long id) {
		if (id == null) {
			return null;
		}
		return cidadeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
				String.format("A cidade com ID %d não foi encontrado no banco de dados.", id)));
	}
}
