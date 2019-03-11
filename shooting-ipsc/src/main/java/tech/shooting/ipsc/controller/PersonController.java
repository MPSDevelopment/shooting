package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.Token;
import tech.shooting.ipsc.bean.PersonBean;
import tech.shooting.ipsc.bean.UpdatePerson;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.PersonRepository;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.PERSON_CONTROLLER)
@Api(value = ControllerAPI.PERSON_CONTROLLER)
@Slf4j
public class PersonController {

	@Autowired
	private PersonRepository personRepository;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new person", notes = "Create new person")
	public ResponseEntity<Person> createPerson (@RequestBody @Valid PersonBean personBean) throws BadRequestException {
		Person person = new Person();
		BeanUtils.copyProperties(personBean, person);
		createPerson(person);
		return new ResponseEntity<>(person, HttpStatus.CREATED);
	}

	private void createPerson (Person person) {
		log.info("Create person with name %s and code ipsc %s", person.getName(), person.getRifleCodeIpsc());
		if(personRepository.findByNameAndRifleCodeIpsc(person.getName(), person.getRifleCodeIpsc()) != null) {
			throw new ValidationException(Person.NAME_AND_IPSC_FIELD, "Person with name %s and rifle ipsc code %s is already exist", person.getName(), person.getRifleCodeIpsc());
		}
		person.setActive(true);
		personRepository.save(person);

	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get person by id", notes = "Return person object")
	public ResponseEntity<Person> getPerson (@PathVariable(value = "personId", required = true) Long personId) throws BadRequestException {
		Person person = personRepository.findById(personId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", personId)));
		return new ResponseEntity<>(person, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_UPDATE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update person", notes = "Return update person object")
	public ResponseEntity<Person> updatePerson (@PathVariable(value = "personId", required = true) Long personId, @RequestBody @Valid UpdatePerson personBean) throws BadRequestException {

		if(!personId.equals(personBean.getId())) {
			throw new BadRequestException(new ErrorMessage("Path personId %s does not match bean personId %s", personId, personBean.getId()));
		}

		Person dbPerson = personRepository.findById(personId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", personId)));
		BeanUtils.copyProperties(personBean, dbPerson);
		personRepository.save(dbPerson);
		return new ResponseEntity<>(dbPerson, HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete person", notes = "Return removed person object")
	public ResponseEntity<Person> deletePerson (@PathVariable(value = "personId", required = true) Long personId) throws BadRequestException {
		Person person = personRepository.findById(personId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", personId)));
		personRepository.delete(person);
		return new ResponseEntity<>(person, HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_ALL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all persons", notes = "Returns all person objects")
	public ResponseEntity<List<Person>> getUsers () throws BadRequestException {
		return new ResponseEntity<>(personRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_ALL_USERS_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get persons by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = "page", description = "Current page number", response = String.class), @ResponseHeader(name = "total", description = "Total " +
		"records in database", response = String.class), @ResponseHeader(name = "pages", description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<Person>> getPersons (@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @PathVariable(value = "pageNumber") Integer page,
	                                                @PathVariable(value = "pageSize") Integer size) throws BadRequestException {
		return PageAble.getPage(page, size, Person.class, personRepository);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all persons count", notes = "Returns all persons count")
	public ResponseEntity<Long> getCount () throws BadRequestException {
		return new ResponseEntity<>(personRepository.count(), HttpStatus.OK);
	}


}
