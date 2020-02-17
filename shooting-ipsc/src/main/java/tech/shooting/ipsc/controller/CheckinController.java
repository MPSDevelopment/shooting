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
import tech.shooting.commons.pojo.Token;
import tech.shooting.commons.pojo.TokenUser;
import tech.shooting.commons.utils.TokenUtils;
import tech.shooting.ipsc.bean.CheckinBean;
import tech.shooting.ipsc.bean.CombatListSearchBean;
import tech.shooting.ipsc.bean.CombatNoteBean;
import tech.shooting.ipsc.bean.NameStatus;
import tech.shooting.ipsc.bean.SearchResult;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.CombatNote;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.service.CheckinService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.CHECKIN_CONTROLLER)
@Api(ControllerAPI.CHECKIN_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class CheckinController {
	
	@Autowired
	private CheckinService checkinService;

	@Autowired
	private TokenUtils tokenUtils;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_BY_DIVISION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list check", notes = "Return list person's from current division")
	public ResponseEntity<List<Person>> getCheckList(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) @NotNull Long id) throws BadRequestException {
		return new ResponseEntity<>(checkinService.getList(id), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_CHECK, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added check", notes = "Return created check")
	public ResponseEntity<List<CheckIn>> createCheck(@RequestHeader(value = Token.TOKEN_HEADER) String token, @RequestBody @Valid List<CheckinBean> bean) throws BadRequestException {
		TokenUser byToken = tokenUtils.getByToken(token);
		return new ResponseEntity<>(checkinService.createCheck(byToken, bean), HttpStatus.CREATED);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_POST_COMBAT_NOTE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added combat note", notes = "Return note object")
	public ResponseEntity<CombatNote> createCombatNote(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) @NotNull Long divisionId, @RequestBody @Valid CombatNoteBean note) throws BadRequestException {
		return new ResponseEntity<>(checkinService.createCombatNote(divisionId, note), HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_COMBAT_NOTE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added combat note", notes = "Return note object")
	public ResponseEntity<List<CombatNote>> getCombatNote(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) @NotNull Long divisionId) throws BadRequestException {
		return new ResponseEntity<>(checkinService.getCombatNote(divisionId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_INTERVAL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get List names of interval's")
	public ResponseEntity<List<String>> getListInterval() {
		return new ResponseEntity<>(checkinService.getInterval(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get List search result")
	public ResponseEntity<List<SearchResult>> getSearchResult(@RequestBody CombatListSearchBean bean) throws BadRequestException {
		return new ResponseEntity<>(checkinService.getSearchResult(bean), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_SEARCH_RESULT_BY_NAMES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get Named list search result")
	public ResponseEntity<List<NameStatus>> getNamedSearchResult(@RequestBody CombatListSearchBean bean) throws BadRequestException {
		return new ResponseEntity<>(checkinService.getSearch(bean), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.CHECKIN_CONTROLLER_GET_LIST_COMBAT_NOTE_BY_DIVISION_BY_DATE_BY_INTERVAL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get List <CombatNote> by division by date by interval")
	public ResponseEntity<List<CombatNote>> getListCombatNoteByDivisionByDateById(@RequestBody CombatListSearchBean searchBean) throws BadRequestException {
		return new ResponseEntity<>(checkinService.getListCombatNote(searchBean), HttpStatus.OK);
	}
}
