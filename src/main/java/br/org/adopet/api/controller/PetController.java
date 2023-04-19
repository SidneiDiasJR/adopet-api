package br.org.adopet.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.org.adopet.api.domain.dto.PetCadastroDTO;
import br.org.adopet.api.domain.dto.PetListagemDTO;
import br.org.adopet.api.domain.model.Abrigo;
import br.org.adopet.api.domain.model.Pet;
import br.org.adopet.api.domain.model.Porte;
import br.org.adopet.api.domain.repository.AbrigoRepository;
import br.org.adopet.api.domain.repository.PetRepository;
import br.org.adopet.api.domain.repository.PorteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "pets")
public class PetController {
	
	@Autowired
	PetRepository petRepository;
	
	@Autowired
	AbrigoRepository abrigoRepository;
	
	@Autowired
	PorteRepository porteRepository;
	
	@GetMapping
	public ResponseEntity<List<PetListagemDTO>> get(){
		List<PetListagemDTO> pets = petRepository.findAll().stream().map(PetListagemDTO::new).toList();
		if(pets.size() == 0) {
			throw new EntityNotFoundException();
		}
		return ResponseEntity.ok(pets);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PetListagemDTO> get(@PathVariable Long id){
		Pet pet = petRepository.getReferenceById(id);
		return ResponseEntity.ok(new PetListagemDTO(pet));
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<PetListagemDTO> post(@RequestBody @Valid PetCadastroDTO dadosPet) {
		Abrigo abrigo = abrigoRepository.getReferenceById(dadosPet.abrigoId());
		Porte porte = porteRepository.getReferenceById(dadosPet.porteId());
		Pet pet = petRepository.save(new Pet(dadosPet, abrigo, porte));
		return ResponseEntity.ok(new PetListagemDTO(pet));
	}
}