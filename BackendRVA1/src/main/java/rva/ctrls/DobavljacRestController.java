package rva.ctrls;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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
import rva.jpa.Dobavljac;
import rva.repository.DobavljacRepository;

@Api(tags = { "Dobavljač CRUD operacije" })
@RestController
@CrossOrigin
public class DobavljacRestController {

	@Autowired
	private DobavljacRepository dobavljacRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@ApiOperation(value = "Vraća kolekciju svih dobavljača iz baze podataka")
	@GetMapping("dobavljac")
	public Collection<Dobavljac> getDobavljaci() {
		return dobavljacRepository.findAll();
	}

	@ApiOperation(value = "Vraća dobavljača iz baze podataka čiji je id vrednost prosleđena kao path varijabla")
	@GetMapping("dobavljac/{id}")
	public Dobavljac getDobavljac(@PathVariable("id") Integer id) {
		return dobavljacRepository.getOne(id);
	}

	@ApiOperation(value = "Vraća kolekciju svih dobavljača iz baze podataka koji u nazivu sadrže string prosleđen kao path varijabla")
	@GetMapping("dobavljacNaziv/{naziv}")
	public Collection<Dobavljac> getDobavljacByNaziv(@PathVariable("naziv") String naziv) {
		return dobavljacRepository.findByNazivContainingIgnoreCase(naziv);
	}

	@ApiOperation(value = "Briše dobavljača iz baze podataka čiji je id vrednost prosleđena kao path varijabla")
	@Transactional
	@DeleteMapping("dobavljac/{id}")
	public ResponseEntity<Dobavljac> deleteDobavljac(@PathVariable("id") Integer id) {
		if (!dobavljacRepository.existsById(id))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		jdbcTemplate.execute(
				"delete from stavka_porudzbine where porudzbina in (select id from porudzbina where dobavljac = " + id
						+ ");");
		jdbcTemplate.execute("delete from porudzbina where dobavljac = " + id);
		dobavljacRepository.deleteById(id);
		if (id == -100)
			jdbcTemplate.execute(
					"INSERT INTO \"dobavljac\"(\"id\", \"naziv\", \"adresa\", \"kontakt\")VALUES(-100, 'Naziv test', 'Adresa test', 'Kontakt test')");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "Upisuje dobavljača u bazu podataka")
	@PostMapping("dobavljac")
	public ResponseEntity<Dobavljac> insertDobavljac(@RequestBody Dobavljac dobavljac) {
		if (!dobavljacRepository.existsById(dobavljac.getId())) {
			dobavljacRepository.save(dobavljac);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@ApiOperation(value = "Modifikuje postojećeg dobavljača u bazi podataka")
	@PutMapping("dobavljac")
	public ResponseEntity<Dobavljac> updateDobavljac(@RequestBody Dobavljac dobavljac) {
		if (!dobavljacRepository.existsById(dobavljac.getId()))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		dobavljacRepository.save(dobavljac);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
