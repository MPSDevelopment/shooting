package tech.shooting.ipsc.controller;

import java.util.List;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import tech.shooting.ipsc.bean.VehicleTypeBean;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.service.VehicleTypeService;

@Controller
@RequestMapping(ControllerAPI.VEHICLE_TYPE_CONTROLLER)
@Api(ControllerAPI.VEHICLE_TYPE_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class VehicleTypeController {

	@Autowired
	private VehicleTypeService service;

	@PreAuthorize("hasRole('ADMIN') or hasRole('JUDGE')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_TYPE_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Return list of type's", notes = "List<WeaponType> or Optional.empty()")
	public ResponseEntity<List<VehicleType>> getAllTypes() {
		return new ResponseEntity<>(service.getAllType(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('JUDGE')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_TYPE_CONTROLLER_GET_BY_ID)
	@ApiOperation(value = "Return type by id", notes = "Type or BadRequestException")
	public ResponseEntity<VehicleType> getTypeById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_TYPE_ID) Long typeId) throws BadRequestException {
		return new ResponseEntity<>(service.getTypeById(typeId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_TYPE_CONTROLLER_POST_TYPE)
	@ApiOperation(value = "Return created type", notes = "Return created name ")
	public ResponseEntity<VehicleType> postType(@RequestBody @Valid VehicleTypeBean bean) {
		return new ResponseEntity<>(service.postType(bean), HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_TYPE_CONTROLLER_PUT_TYPE)
	@ApiOperation(value = "Return created type updated", notes = "Return created name  Updated")
	public ResponseEntity<VehicleType> postType(@PathVariable(value = ControllerAPI.PATH_VARIABLE_TYPE_ID) long typeId, @RequestBody @Valid VehicleTypeBean bean) throws BadRequestException {
		return new ResponseEntity<>(service.updateType(typeId, bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VEHICLE_TYPE_CONTROLLER_DELETE_TYPE_BY_ID)
	@ApiOperation(value = "Delete type return status Ok")
	public ResponseEntity<SuccessfulMessage> deleteType(@PathVariable(value = ControllerAPI.PATH_VARIABLE_TYPE_ID) long typeId) throws BadRequestException {
		service.deleteType(typeId);
		return new ResponseEntity<>(new SuccessfulMessage("Vehicle type %s has been successfully deleted", typeId), HttpStatus.OK);
	}

}
