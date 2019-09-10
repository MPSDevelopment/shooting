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
import tech.shooting.ipsc.bean.WeaponBean;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.service.WeaponService;

@Controller
@RequestMapping(ControllerAPI.WEAPON_CONTROLLER)
@Api(ControllerAPI.WEAPON_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class WeaponController {

	@Autowired
	private WeaponService weaponService;

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Return list of weapon's", notes = "List<Weapon> or Optional.empty()")
	public ResponseEntity<List<Weapon>> getAllWeapon() {
		return new ResponseEntity<>(weaponService.getAll(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_BY_ID)
	@ApiOperation(value = "Return weapon by id", notes = "Weapon or BadRequestException")
	public ResponseEntity<Weapon> getWeaponById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_ID) Long weaponId) throws BadRequestException {
		return new ResponseEntity<>(weaponService.getWeaponById(weaponId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_DIVISION_ID)
	@ApiOperation(value = "Return all weapon by division")
	public ResponseEntity<List<Weapon>> getWeaponByDivision(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long divisionId) throws BadRequestException {
		return new ResponseEntity<>(weaponService.getAllByDivision(divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_OWNER_ID)
	@ApiOperation(value = "Return all weapon's  where owner person with id")
	public ResponseEntity<List<Weapon>> getWeaponByPerson(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(weaponService.getAllByPerson(personId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_GET_ALL_BY_PERSON_NAME_AND_DIVISION_ID)
	@ApiOperation(value = "Return all weapon's  where owner find by  person name and division id")
	public ResponseEntity<List<Weapon>> getWeaponByPersonNameAndDivisionID(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_NAME) @NotEmpty String personName,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) long divisionId) throws BadRequestException {
		return new ResponseEntity<>(weaponService.getAllByPersonWeapon(personName, divisionId), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Return created weapon if exist update", notes = "Return created weapon if exist update")
	public ResponseEntity<Weapon> postWeapon(@RequestBody @Valid WeaponBean bean) throws BadRequestException {
		return new ResponseEntity<>(weaponService.postWeapon(bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_DELETE_WEAPON_BY_ID)
	@ApiOperation(value = "Delete type return status Ok")
	public ResponseEntity<SuccessfulMessage> deleteWeapon(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_ID) long weaponId) {
		weaponService.deleteWeapon(weaponId);
		return new ResponseEntity<>(new SuccessfulMessage("Weapon %s has been deleted", weaponId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_OWNER)
	@ApiOperation(value = "Return updated weapon row", notes = "Return updated Weapon object")
	public ResponseEntity<Weapon> postWeaponAddOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_ID) Long weaponId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(weaponService.addOwnerToWeapon(weaponId, personId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_REMOVE_OWNER)
	@ApiOperation(value = "Return updated weapon row, with owner null", notes = "Return updated Weapon object")
	public ResponseEntity<Weapon> postWeaponRemoveOwner(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_ID) Long weaponId) throws BadRequestException {
		return new ResponseEntity<>(weaponService.addOwnerToWeapon(weaponId, null), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.WEAPON_CONTROLLER_POST_WEAPON_ADD_FIRED_COUNT)
	@ApiOperation(value = "Return updated weapon row, with owner null", notes = "Return updated Weapon object")
	public ResponseEntity<Weapon> postWeaponAddShootings(@PathVariable(value = ControllerAPI.PATH_VARIABLE_WEAPON_ID) Long weaponId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_FIRED_COUNT) Integer firedCount)
			throws BadRequestException {
		return new ResponseEntity<>(weaponService.addNumberOfShootingForWeapon(weaponId, firedCount), HttpStatus.OK);
	}
}