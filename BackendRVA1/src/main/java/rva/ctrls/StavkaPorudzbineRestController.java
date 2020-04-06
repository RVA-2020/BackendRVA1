package rva.ctrls;

import java.math.BigDecimal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import rva.jpa.Porudzbina;
import rva.jpa.StavkaPorudzbine;
import rva.repository.PorudzbinaRepository;
import rva.repository.StavkaPorudzbineRepository;

@Api(tags = { "Stavka Porudžbine CRUD operacije" })
@RestController
public class StavkaPorudzbineRestController {
	
	@Autowired
	private StavkaPorudzbineRepository stavkaPorudzbineRepository;
	@Autowired
	private PorudzbinaRepository porudzbinaRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@ApiOperation(value = "Vraća kolekciju svih stavki porudžbina iz baze podataka")
	@GetMapping(value = "stavkaPorudzbine")
	public Collection<StavkaPorudzbine> getStavkePorudzbine(){
		return stavkaPorudzbineRepository.findAll();
	}

	@ApiOperation(value = "Vraća stavku porudžbine iz baze podataka čiji je id vrednost prosleđena kao path varijabla")
	@GetMapping(value = "stavkaPorudzbine/{id}")
	public ResponseEntity<StavkaPorudzbine> getStavkaPorudzbine(@PathVariable("id") Integer id){
		StavkaPorudzbine stavkaPorudzbine = stavkaPorudzbineRepository.getOne(id);
		return new ResponseEntity<StavkaPorudzbine>(stavkaPorudzbine, HttpStatus.OK);
	}

	@ApiOperation(value = "Vraća kolekciju stavki porudzbina iz baze podataka čiji je id porudžbine vrednost prosleđena kao path varijabla")
	@GetMapping(value = "stavkeZaPorudzbinaId/{id}")
	public Collection<StavkaPorudzbine> stavkaPoPorudzbiniId(@PathVariable("id") int id){
		Porudzbina p = porudzbinaRepository.getOne(id);
		return stavkaPorudzbineRepository.findByPorudzbina(p);
	}
	
	@ApiOperation(value = "Vraća kolekciju stavki porudžbina iz baze podataka čija je cena manja od cene prosleđene kao path varijabla")
	@GetMapping(value = "stavkaPorudzbineCena/{cena}")
	public Collection<StavkaPorudzbine> getStavkaPorudzbineCena(@PathVariable("cena") BigDecimal cena){
		return stavkaPorudzbineRepository.findByCenaLessThanOrderById(cena);
	}

	@ApiOperation(value = "Briše stavku porudžbine iz baze podataka čiji je id vrednost prosleđena kao path varijabla")
	@DeleteMapping (value = "stavkaPorudzbine/{id}")
	public ResponseEntity<StavkaPorudzbine> deleteStavkaPorudzbine(@PathVariable("id") Integer id){
		if(!stavkaPorudzbineRepository.existsById(id))
			return new ResponseEntity<StavkaPorudzbine>(HttpStatus.NO_CONTENT);
		stavkaPorudzbineRepository.deleteById(id);
		if (id == -100)
			jdbcTemplate.execute(
					"INSERT INTO \"stavka_porudzbine\"(\"id\", \"porudzbina\", \"redni_broj\", \"artikl\", \"kolicina\", \"jedinica_mere\", \"cena\")\n"
							+ "VALUES (-100, 1, 3, 3, 15, 'komad', 80);");

		return new ResponseEntity<StavkaPorudzbine>(HttpStatus.OK);
	}

	@ApiOperation(value = "Upisuje stavku porudžbine u bazu podataka")
	@PostMapping(value = "stavkaPorudzbine")
	public ResponseEntity<Void> insertStavkaPorudzbine(@RequestBody StavkaPorudzbine stavkaPorudzbine){
		if(stavkaPorudzbineRepository.existsById(stavkaPorudzbine.getId()))
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		stavkaPorudzbine.setRedniBroj(stavkaPorudzbineRepository.nextRBr(stavkaPorudzbine.getPorudzbina().getId()));
		stavkaPorudzbineRepository.save(stavkaPorudzbine);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@ApiOperation(value = "Modifikuje postojeću stavku porudžbine u bazi podataka")
	@PutMapping(value = "stavkaPorudzbine")
	public ResponseEntity<Void> updateStavkaPorudzbine(@RequestBody StavkaPorudzbine stavkaPorudzbine){
		if(!stavkaPorudzbineRepository.existsById(stavkaPorudzbine.getId()))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		stavkaPorudzbineRepository.save(stavkaPorudzbine);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}