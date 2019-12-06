package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.service.TagService;

@Controller
@RequestMapping(ControllerAPI.TAG_CONTROLLER)
@Api(ControllerAPI.TAG_CONTROLLER)
@Slf4j
public class TagController {

	@Autowired
	private TagService service;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_STATUS)
	@ApiOperation(value = "Return default settings")
	public ResponseEntity<SuccessfulMessage> getStatus() {
		var status = service.getStatus();
		if (status) {
			return new ResponseEntity<>(new SuccessfulMessage("Status connected"), HttpStatus.OK);
		}
		return new ResponseEntity<>(new SuccessfulMessage("Status disconnected"), HttpStatus.OK);
	}
}
