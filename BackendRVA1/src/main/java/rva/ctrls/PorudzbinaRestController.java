package rva.ctrls;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import rva.repository.PorudzbinaRepository;

@Api(tags = { "Porudžbina CRUD operacije" })
@RestController
@CrossOrigin
public class PorudzbinaRestController {
	
	@Autowired
	private PorudzbinaRepository porudzbinaRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@ApiOperation(value = "Vraća kolekciju svih porudžbina iz baze podataka")
	@GetMapping("porudzbina")
	public Collection<Porudzbina> getPorudzbine(){
		return porudzbinaRepository.findAll();
	}
	
	@ApiOperation(value = "Vraća porudžbinu iz baze podataka čiji je id vrednost prosleđena kao path varijabla")
	@GetMapping("porudzbina/{id}")
	public Porudzbina getPorudzbina(@PathVariable ("id") Integer id){
		return porudzbinaRepository.getOne(id);
	}

	@ApiOperation(value = "Vraća kolekciju svih porudžbina iz baze podataka koje su plaćene")
	@GetMapping("porudzbinePlacene")
	public Collection<Porudzbina> getPorudzbineByPlaceno(){
		return porudzbinaRepository.findByPlacenoTrue();
	}
	
	@ApiOperation(value = "Briše porudžbinu iz baze podataka čiji je id vrednost prosleđena kao path varijabla")
	@Transactional
	@DeleteMapping("porudzbina/{id}")
	public ResponseEntity<Porudzbina> deletePorudzbina(@PathVariable ("id") Integer id){
		if(!porudzbinaRepository.existsById(id))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		jdbcTemplate.execute("delete from stavka_porudzbine where porudzbina = "+id);	
		if (id != -100)
			porudzbinaRepository.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "Upisuje porudžbinu u bazu podataka")
	@PostMapping("porudzbina")
	public ResponseEntity<Porudzbina> insertPorudzbina(@RequestBody Porudzbina porudzbina){
		if(!porudzbinaRepository.existsById(porudzbina.getId())){
			porudzbinaRepository.save(porudzbina);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@ApiOperation(value = "Modifikuje postojeću porudžbinu u bazi podataka")
	@PutMapping("porudzbina")
	public ResponseEntity<Porudzbina> updatePorudzbina(@RequestBody Porudzbina porudzbina){
		if(!porudzbinaRepository.existsById(porudzbina.getId()))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		porudzbinaRepository.save(porudzbina);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	 
}