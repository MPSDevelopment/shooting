package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.PersonBean;
import tech.shooting.ipsc.bean.UpdatePerson;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.TypePresent;
import tech.shooting.ipsc.service.PersonService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.PERSON_CONTROLLER)
@Api(value = ControllerAPI.PERSON_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class PersonController {
	
	@Autowired
	private PersonService personService;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_PERSON, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new person", notes = "Create new person")
	public ResponseEntity<Person> createPerson (@RequestBody @Valid PersonBean personBean) throws BadRequestException {
		return new ResponseEntity<>(personService.createPerson(personBean), HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get person by id", notes = "Return person object")
	public ResponseEntity<Person> getPerson (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(personService.getPersonByIdIfExist(personId), HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_PUT_PERSON, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update person", notes = "Return update person object")
	public ResponseEntity<Person> updatePerson (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId, @RequestBody @Valid UpdatePerson personBean) throws BadRequestException {
		return new ResponseEntity<>(personService.updatePerson(personId, personBean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_DELETE_PERSON, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete person", notes = "Return removed person object")
	public ResponseEntity<Void> deletePerson (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		personService.removePersonIfExist(personId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or  hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSONS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all persons", notes = "Returns all person objects")
	public ResponseEntity<List<Person>> getUsers () {
		return new ResponseEntity<>(personService.getAllPerson(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_USERS_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get persons by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
		@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
		@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<Person>> getPersons (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return personService.getPersonByPage(page, size);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all persons count", notes = "Returns all persons count")
	public ResponseEntity<Long> getCount () {
		return new ResponseEntity<>(personService.getCount(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or  hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PRESENT_ENUM)
	@ApiOperation(value = "Get all form's of present type", notes = "Return list TypePresent object")
	public ResponseEntity<List<TypePresent>> getTypePresent () {
		return new ResponseEntity<>(personService.getTypePresent(), HttpStatus.OK);
	}


}
