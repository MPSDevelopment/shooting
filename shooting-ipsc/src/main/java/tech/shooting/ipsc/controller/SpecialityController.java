package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.SpecialityBean;
import tech.shooting.ipsc.pojo.Speciality;
import tech.shooting.ipsc.service.SpecialityService;

import java.util.List;

@Controller
@RequestMapping(ControllerAPI.SPECIALITY_CONTROLLER)
@Api(value = ControllerAPI.SPECIALITY_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SpecialityController {

	@Autowired
	private SpecialityService specialityService;

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get  list speciality")
	public ResponseEntity<List<Speciality>> getAllSpeciality() {
		return new ResponseEntity<>(specialityService.getAll(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_GET_SPECIALITY_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get speciality by id or throws BadRequestException")
	public ResponseEntity<Speciality> getSpecialityById(@PathVariable (value = ControllerAPI.PATH_VARIABLE_SPECIALITY_ID)Long specialityId) throws BadRequestException {
		return new ResponseEntity<>(specialityService.speciality(specialityId), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_DELETE_SPECIALITY_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete speciality by id or throws BadRequestException if not exist")
	public ResponseEntity deleteSpecialityById(@PathVariable (value = ControllerAPI.PATH_VARIABLE_SPECIALITY_ID)Long specialityId) throws BadRequestException {
		specialityService.deleteSpeciality(specialityId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_POST_SPECIALITY, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Create speciality or throws BadRequestException if exist")
	public ResponseEntity<Speciality> postSpeciality(@RequestBody SpecialityBean bean) {
		return new ResponseEntity<>(specialityService.createSpeciality(bean), HttpStatus.CREATED);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.SPECIALITY_CONTROLLER_PUT_SPECIALITY, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update speciality or throws BadRequestException if not exist")
	public ResponseEntity<Speciality> putSpeciality(@PathVariable(value = ControllerAPI.PATH_VARIABLE_SPECIALITY_ID)Long specialityId, @RequestBody SpecialityBean bean) throws BadRequestException {
		return new ResponseEntity<>(specialityService.updateSpeciality(specialityId, bean), HttpStatus.OK);
	}
}
