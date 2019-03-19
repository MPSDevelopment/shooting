package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.service.DivisionService;

import javax.validation.Valid;

@Controller
@RequestMapping(ControllerAPI.DIVISION_CONTROLLER)
@Api(value = ControllerAPI.DIVISION_CONTROLLER)
@Slf4j
public class DivisionController {
	@Autowired
	private DivisionService divisionService;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_POST_DIVISION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new division", notes = "Return created division")
	public ResponseEntity<DivisionBean> createDivision (@RequestBody @Valid DivisionBean divisionBean) throws BadRequestException {
		return new ResponseEntity<>(divisionService.createDivision(divisionBean, divisionBean.getParent()), HttpStatus.CREATED);
	}
}
