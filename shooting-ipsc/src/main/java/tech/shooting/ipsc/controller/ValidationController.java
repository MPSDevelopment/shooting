package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.ipsc.bean.ValidationBean;
import tech.shooting.ipsc.service.ValidationService;

import java.util.Map;

@Controller
@RequestMapping(ControllerAPI.VALIDATION_CONTROLLER)
@Api(value = ControllerAPI.VALIDATION_CONTROLLER)
@Slf4j
public class ValidationController {
	
	@Autowired
	private ValidationService validationService;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.VALIDATION_CONTROLLER_GET_VALIDATIONS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all validations", notes = "Returns all validation objects")
	public ResponseEntity<Map<String, Map<String, ValidationBean>>> getUsers () {
		return new ResponseEntity<>(validationService.getConstraintsForPackage("tech.shooting.ipsc.bean", "tech.shooting.ipsc.pojo"), HttpStatus.OK);
	}
}
