package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.pojo.Tag;
import tech.shooting.ipsc.pojo.TagEpc;
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

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_MODE)
	@ApiOperation(value = "Return server mode")
	public ResponseEntity<String> getMode() {
		return new ResponseEntity<>("Online", HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_CODES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get map codes")
	public ResponseEntity<Map<String, Tag>> getCodes() {
		return new ResponseEntity<>(service.getMap(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_CLEAR, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Clear map codes")
	public ResponseEntity<SuccessfulMessage> clear() {
		service.clear();
		return new ResponseEntity<>(new SuccessfulMessage("Tags cleared"), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_STOP, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Stop sending data")
	public ResponseEntity<SuccessfulMessage> stop() {
		service.stopSending();
		return new ResponseEntity<>(new SuccessfulMessage("Stop sending data"), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_START, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Start sending data")
	public ResponseEntity<SuccessfulMessage> start(@PathVariable(value = ControllerAPI.PATH_VARIABLE_COUNT) Integer laps) {
		service.startSending(laps);
		return new ResponseEntity<>(new SuccessfulMessage("Start sending data"), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_POST_NEW_EPC, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SuccessfulMessage> writeNewEPCCode(@RequestBody TagEpc tagEpc) throws SocketException {

//		var tag = new com.impinj.octane.Tag();

		service.rewriteEPCRequest(tagEpc);
//		service.rewriteETC(tagEpc);
		return new ResponseEntity<>(new SuccessfulMessage("EPC was rewrite", service.getTagIp(), service.getFirstNonLoopbackAddress()), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_CODE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get map codes")
	public ResponseEntity<Tag> getCode() {
		var tag = new Tag();
		tag.setCode(service.readCode());
		return new ResponseEntity<>(tag, HttpStatus.OK);
	}

}
