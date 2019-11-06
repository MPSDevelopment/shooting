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
import tech.shooting.ipsc.bean.CommunicationEquipmentBean;
import tech.shooting.ipsc.pojo.CommunicationEquipment;
import tech.shooting.ipsc.service.CommunicationEquipmentService;

@Controller
@RequestMapping(ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER)
@Api(ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class CommunicationEquipmentController {

	@Autowired
	private CommunicationEquipmentService service;

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Return list of equipment")
	public ResponseEntity<List<CommunicationEquipment>> getAll() {
		return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_GET_BY_ID)
	@ApiOperation(value = "Return vehicle by id", notes = "Vehicle or BadRequestException")
	public ResponseEntity<CommunicationEquipment> getById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_EQUIPMENT_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL_BY_DIVISION_ID)
	@ApiOperation(value = "Return all vehicles by division")
	public ResponseEntity<List<CommunicationEquipment>> getByDivision(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long divisionId) throws BadRequestException {
		return new ResponseEntity<>(service.getAllByDivision(divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL_BY_OWNER_ID)
	@ApiOperation(value = "Return all vehicle  where owner person with id")
	public ResponseEntity<List<CommunicationEquipment>> getWeaponByPerson(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(service.getAllByPerson(personId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID)
	@ApiOperation(value = "Return all equipments  where owner find by  person name and division id")
	public ResponseEntity<List<CommunicationEquipment>> getWeaponByPersonNameAndDivisionID(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_NAME) @NotEmpty String personName,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) long divisionId) throws BadRequestException {
		return new ResponseEntity<>(service.getAllByPerson(personName, divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return created equipment if exist update", notes = "Return created equipment if exist update")
	public ResponseEntity<CommunicationEquipment> post(@RequestBody @Valid CommunicationEquipmentBean bean) throws BadRequestException {
		return new ResponseEntity<>(service.put(bean), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return created equipment if exist update", notes = "Return created equipment if exist update")
	public ResponseEntity<CommunicationEquipment> put(@RequestBody @Valid CommunicationEquipmentBean bean) throws BadRequestException {
		return new ResponseEntity<>(service.put(bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_DELETE_BY_ID)
	@ApiOperation(value = "Delete type return status Ok")
	public ResponseEntity<SuccessfulMessage> deleteWeapon(@PathVariable(value = ControllerAPI.PATH_VARIABLE_EQUIPMENT_ID) long weaponId) {
		service.delete(weaponId);
		return new ResponseEntity<>(new SuccessfulMessage("Equipment %s has been deleted", weaponId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_POST_ADD_OWNER)
	@ApiOperation(value = "Return updated equipment", notes = "Return updated vehicle object")
	public ResponseEntity<CommunicationEquipment> postAddOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_EQUIPMENT_ID) Long weaponId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(service.addOwner(weaponId, personId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMMUNICATION_EQUIPMENT_CONTROLLER_POST_REMOVE_OWNER)
	@ApiOperation(value = "Return updated equipment, with owner null", notes = "Return updated vehicle object")
	public ResponseEntity<CommunicationEquipment> postWeaponRemoveOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_EQUIPMENT_ID) Long weaponId) throws BadRequestException {
		return new ResponseEntity<>(service.addOwner(weaponId, null), HttpStatus.OK);
	}
}