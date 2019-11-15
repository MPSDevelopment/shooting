package tech.shooting.ipsc.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.AnimalBean;
import tech.shooting.ipsc.bean.EquipmentBean;
import tech.shooting.ipsc.pojo.Animal;
import tech.shooting.ipsc.pojo.Equipment;
import tech.shooting.ipsc.service.AnimalService;
import tech.shooting.ipsc.service.EquipmentService;

@Controller
@RequestMapping(ControllerAPI.ANIMAL_CONTROLLER)
@Api(ControllerAPI.ANIMAL_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AnimalController {

	@Autowired
	private AnimalService service;

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_GET_ALL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return list of animals")
	public ResponseEntity<List<Animal>> getAll() {
		return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_GET_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return animal by id", notes = "Vehicle or BadRequestException")
	public ResponseEntity<Animal> getById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_ANIMAL_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_GET_ALL_BY_DIVISION_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return all animals by division")
	public ResponseEntity<List<Animal>> getByDivision(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long divisionId) throws BadRequestException {
		return new ResponseEntity<>(service.getAllByDivision(divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_GET_ALL_BY_OWNER_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return all animals by owner")
	public ResponseEntity<List<Animal>> getByPerson(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(service.getAllByPerson(personId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return all animals by owner and division id")
	public ResponseEntity<List<Animal>> getByPersonNameAndDivisionID(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_NAME) @NotEmpty String personName,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) long divisionId) throws BadRequestException {
		return new ResponseEntity<>(service.getAllByPerson(personName, divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return created animal")
	public ResponseEntity<Animal> post(@RequestBody @Valid AnimalBean bean) throws BadRequestException {
		return new ResponseEntity<>(service.save(bean), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return edited animal")
	public ResponseEntity<Animal> put(@RequestBody @Valid AnimalBean bean) throws BadRequestException {
		return new ResponseEntity<>(service.save(bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_DELETE_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete animal returns status Ok")
	public ResponseEntity<SuccessfulMessage> delete(@PathVariable(value = ControllerAPI.PATH_VARIABLE_ANIMAL_ID) long id) {
		service.delete(id);
		return new ResponseEntity<>(new SuccessfulMessage("Animal %s has been deleted", id), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_POST_ADD_OWNER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return updated animal", notes = "Return updated vehicle object")
	public ResponseEntity<Animal> postAddOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_ANIMAL_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(service.addOwner(id, personId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.ANIMAL_CONTROLLER_POST_REMOVE_OWNER, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return updated animal, with owner null")
	public ResponseEntity<Animal> postRemoveOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_ANIMAL_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(service.addOwner(id, null), HttpStatus.OK);
	}
}