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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.VehicleBean;
import tech.shooting.ipsc.pojo.Vehicle;
import tech.shooting.ipsc.service.VehicleService;

@Controller
@RequestMapping(ControllerAPI.VEHICLE_CONTROLLER)
@Api(ControllerAPI.VEHICLE_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class VehicleController {

	@Autowired
	private VehicleService vehicleService;

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Return list of vehicles")
	public ResponseEntity<List<Vehicle>> getAll() {
		return new ResponseEntity<>(vehicleService.getAll(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_BY_ID)
	@ApiOperation(value = "Return vehicle by id", notes = "Vehicle or BadRequestException")
	public ResponseEntity<Vehicle> getById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_VEHICLE_ID) Long weaponId) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.getVehicleById(weaponId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_DIVISION_ID)
	@ApiOperation(value = "Return all vehicles by division")
	public ResponseEntity<List<Vehicle>> getWeaponByDivision(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long divisionId) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.getAllByDivision(divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_OWNER_ID)
	@ApiOperation(value = "Return all vehicle  where owner person with id")
	public ResponseEntity<List<Vehicle>> getWeaponByPerson(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.getAllByPerson(personId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID)
	@ApiOperation(value = "Return all vehicles  where owner find by  person name and division id")
	public ResponseEntity<List<Vehicle>> getWeaponByPersonNameAndDivisionID(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_NAME) @NotEmpty String personName,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) long divisionId) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.getAllByPerson(personName, divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return created vehicle if exist update", notes = "Return created vehicle if exist update")
	public ResponseEntity<Vehicle> post(@RequestBody @Valid VehicleBean bean) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.post(bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_DELETE_BY_ID)
	@ApiOperation(value = "Delete type return status Ok")
	public ResponseEntity<SuccessfulMessage> deleteWeapon(@PathVariable(value = ControllerAPI.PATH_VARIABLE_VEHICLE_ID) long weaponId) {
		vehicleService.delete(weaponId);
		return new ResponseEntity<>(new SuccessfulMessage("Weapon %s has been deleted", weaponId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_OWNER)
	@ApiOperation(value = "Return updated vehicle row", notes = "Return updated vehicle object")
	public ResponseEntity<Vehicle> postAddOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_VEHICLE_ID) Long weaponId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.addOwner(weaponId, personId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_REMOVE_OWNER)
	@ApiOperation(value = "Return updated vehicle row, with owner null", notes = "Return updated vehicle object")
	public ResponseEntity<Vehicle> postWeaponRemoveOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_VEHICLE_ID) Long weaponId) throws BadRequestException {
		return new ResponseEntity<>(vehicleService.addOwner(weaponId, null), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_CONTROLLER_POST_ADD_COUNT)
	@ApiOperation(value = "Return updated vehicle row, with owner null", notes = "Return updated vehicle object")
	public ResponseEntity<Vehicle> postWeaponAddShootings(@PathVariable(value = ControllerAPI.PATH_VARIABLE_VEHICLE_ID) Long weaponId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_FIRED_COUNT) Integer firedCount)
			throws BadRequestException {
		return new ResponseEntity<>(vehicleService.addNumberOfShootingForWeapon(weaponId, firedCount), HttpStatus.OK);
	}
}