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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.pojo.TokenUser;
import tech.shooting.ipsc.bean.CheckinBean;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.security.TokenUtils;
import tech.shooting.ipsc.service.CheckinService;

import javax.validation.Valid;

@Controller
@RequestMapping(ControllerAPI.CHECKIN_CONTROLLER)
@Api(ControllerAPI.CHECKIN_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('User')")
public class CheckinController {
	@Autowired
	private CheckinService checkinService;

	@Autowired
	private TokenUtils tokenUtils;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_CHECK, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added check", notes = "Return created check")
	public ResponseEntity<CheckIn> createCheck (@RequestHeader(value = Token.TOKEN_HEADER) String token, @RequestBody @Valid CheckinBean bean) throws BadRequestException {
		TokenUser byToken = tokenUtils.getByToken(token);
		return new ResponseEntity<>(checkinService.createCheck(byToken, bean), HttpStatus.CREATED);
	}
}
