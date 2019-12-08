package tech.shooting.tag.controller;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.tag.pojo.SuccessfulMessage;
import tech.shooting.tag.service.TagService;

import java.net.SocketException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerAPI.TAG_CONTROLLER)
@Slf4j
public class TagController {

	@Autowired
	private TagService service;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.TAG_CONTROLLER_GET_STATUS)
	public ResponseEntity<SuccessfulMessage> getStatus() throws SocketException {
		var status = service.getStatus();
		if (status) {
			return new ResponseEntity<>(new SuccessfulMessage("Status connected Tag Ip %s Local ip %s", service.getTagIp(), service.getFirstNonLoopbackAddress()), HttpStatus.OK);
		}
		return new ResponseEntity<>(new SuccessfulMessage("Status disconnected  Tag Ip %s Local ip %s", service.getTagIp(), service.getFirstNonLoopbackAddress()), HttpStatus.OK);
	}
}
