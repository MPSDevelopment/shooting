package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.commons.pojo.Token;
import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.bean.StandardBean;
import tech.shooting.ipsc.bean.StandardScoreBean;
import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.enums.CompetitionClassEnum;
import tech.shooting.ipsc.enums.StandardPassEnum;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.pojo.QuizScore;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.pojo.StandardScore;
import tech.shooting.ipsc.service.StandardService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.STANDARD_CONTROLLER)
@Api(value = ControllerAPI.STANDARD_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class StandardController {
	@Autowired
	private StandardService standardService;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Get list all standard")
	public ResponseEntity<List<Standard>> getAllStandards() {
		return new ResponseEntity<>(standardService.getAllStandards(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT)
	@ApiOperation(value = "Get list standards by subject")
	public ResponseEntity<List<Standard>> getStandardsBySubject(@PathVariable(value = ControllerAPI.PATH_VARIABLE_SUBJECT_ID) Long subjectId) throws BadRequestException {
		return new ResponseEntity<>(standardService.getStandardsBySubject(subjectId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID)
	@ApiOperation(value = "Get standard by id")
	public ResponseEntity<Standard> getStandardById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) throws BadRequestException {
		return new ResponseEntity<>(standardService.getStandardById(standardId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_STANDARD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get created standard")
	public ResponseEntity<Standard> postStandard(@RequestBody @Valid StandardBean bean) throws BadRequestException {
		return new ResponseEntity<>(standardService.postStandard(bean), HttpStatus.CREATED);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Get updated standard")
	public ResponseEntity<Standard> putStandard(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId, @RequestBody @Valid StandardBean bean) throws BadRequestException {
		return new ResponseEntity<>(standardService.putStandard(standardId, bean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID)
	@ApiOperation(value = "Delete standard")
	public ResponseEntity<SuccessfulMessage> deleteStandardById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) {
		standardService.deleteStandardById(standardId);
		return new ResponseEntity<>(new SuccessfulMessage("Standard was successfully deleted"), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_SCORE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Submit a score")
	public ResponseEntity<StandardScore> postScore(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId, @RequestBody @Valid StandardScoreBean score) throws BadRequestException {
		return new ResponseEntity<>(standardService.addScore(standardId, score), HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get a score", notes = "Return score object")
	public ResponseEntity<StandardScore> getScore(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(standardService.getScore(standardId, personId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_LIST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get a score list", notes = "Return score list")
	public ResponseEntity<List<StandardScore>> getScoreList(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId)
			throws BadRequestException {
		return new ResponseEntity<>(standardService.getScoreList(standardId, personId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_STANDARD_LIST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get a score list", notes = "Return score list")
	public ResponseEntity<List<StandardScore>> getScoreStandardList(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) throws BadRequestException {
		return new ResponseEntity<>(standardService.getScoreStandardList(standardId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_PERSON_LIST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get a score list", notes = "Return score list")
	public ResponseEntity<List<StandardScore>> getScorePersonList(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(standardService.getScorePersonList(personId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_QUERY_LIST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get a score list for a query", notes = "Return score list")
	public ResponseEntity<List<StandardScore>> getScoreQueryList(@RequestBody StandardScoreRequest query) throws BadRequestException {
		return new ResponseEntity<>(standardService.getScoreList(query), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_SCORE_QUERY_LIST_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get standard scores by page")
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", responseHeaders = { @ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class) }) })
	public ResponseEntity<List<StandardScore>> getScoreQueryListByPage(@RequestBody StandardScoreRequest query, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return standardService.getScoreList(query, page, size);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_START_IMITATOR, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Start standard running imitator")
	public ResponseEntity<SuccessfulMessage> postStartImitator(@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) throws BadRequestException {
		standardService.startImitator(standardId);
		return new ResponseEntity<>(new SuccessfulMessage("Standard running imitator was successfully started"), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_START_IMITATOR_ONLY_CODES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation("Start standard running imitator only codes")
	public ResponseEntity<SuccessfulMessage> postStartImitatorOnlyCodes(@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) throws BadRequestException {
		standardService.startImitatorOnlyCodes(standardId);
		return new ResponseEntity<>(new SuccessfulMessage("Standard running imitator only codes was successfully started"), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_PASS_ENUM, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "List of standard pass values")
	public ResponseEntity<StandardPassEnum[]> getPasses() {
		return new ResponseEntity<>(StandardPassEnum.values(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_UNIT_ENUM, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "List of standard unit values")
	public ResponseEntity<UnitEnum[]> getUnits() {
		return new ResponseEntity<>(UnitEnum.values(), HttpStatus.OK);
	}

}
