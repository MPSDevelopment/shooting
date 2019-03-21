package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.bean.CompetitorMark;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.service.CompetitionService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.COMPETITION_CONTROLLER)
@Api(value = ControllerAPI.COMPETITION_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class CompetitionController {
	@Autowired
	private CompetitionService competitionService;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new Competition", notes = "Creates new Competition")
	public ResponseEntity<Competition> createCompetition (@RequestBody @Valid CompetitionBean competitionBean) throws BadRequestException {
		return new ResponseEntity<>(competitionService.createCompetition(competitionBean), HttpStatus.CREATED);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update competition", notes = "Return update competition object")
	public ResponseEntity<Competition> updateCompetition (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid CompetitionBean competition) throws BadRequestException {
		return new ResponseEntity<>(competitionService.updateCompetition(id, competition), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by id", notes = "Return competition object")
	public ResponseEntity<Competition> getCompetitionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(competitionService.checkCompetition(id), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Remove competition", notes = "Return removed competition object")
	public ResponseEntity<Void> deleteCompetitionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id) throws BadRequestException {
		competitionService.removeCompetition(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get count competitions", notes = "Return long count of competitions")
	public ResponseEntity<Integer> getCount () {
		return new ResponseEntity<>(competitionService.getCount(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITIONS, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list competitions", notes = "Return List competition object")
	public ResponseEntity<List<Competition>> getAllCompetitions () {
		return new ResponseEntity<>(competitionService.getAll(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
		@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
		@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class)})})
	public ResponseEntity getCompetitionsByPage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return competitionService.getCompetitionsByPage(page, size);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stages from competition", notes = "Return list of stages object")
	public ResponseEntity<List<Stage>> getStagesFromCompetitionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(competitionService.getStages(id), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added list stages to exist stages", notes = "Return list of stages")
	public ResponseEntity<List<Stage>> postStages (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid List<Stage> toAdded) throws BadRequestException {
		return new ResponseEntity<>(competitionService.addedAllStages(id, toAdded), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add stage to exist stages", notes = "Return created stage")
	public ResponseEntity<Stage> postStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid Stage toAdded) throws BadRequestException {
		return new ResponseEntity<>(competitionService.addStage(id, toAdded), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stage by id", notes = "Return stage object")
	public ResponseEntity<Stage> getStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long competitionId,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_STAGE_ID) Long stageId) throws BadRequestException {
		return new ResponseEntity<>(competitionService.getStage(competitionId, stageId), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete stage by id", notes = "Return removed stage object")
	public ResponseEntity<Void> deleteStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long competitionId,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_STAGE_ID) Long stageId) throws BadRequestException {
		competitionService.deleteStage(competitionId, stageId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update stage ", notes = "Return updated stage object")
	public ResponseEntity<Stage> putStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long competitionId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_STAGE_ID) Long stageId,
		@RequestBody @Valid Stage stage) throws BadRequestException {
		return new ResponseEntity<>(competitionService.updateStage(competitionId, stageId, stage), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get const of ClassifierIPSC standard", notes = "Return list of ClassifierIPSC")
	public ResponseEntity<List<Stage>> getEnum () {
		return new ResponseEntity<>(competitionService.getEnum(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITORS, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list of competitors.", notes = "Return list competitors")
	public ResponseEntity<List<Competitor>> getCompetitors (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(competitionService.checkCompetition(id).getCompetitors(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added competitor to the list competitors", notes = "Return competitor object")
	public ResponseEntity<Competitor> postCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid Competitor competitor) throws BadRequestException {
		return new ResponseEntity<>(competitionService.addedCompetitor(id, competitor), HttpStatus.CREATED);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Remove competitor from competition", notes = "Return removed competitor")
	public ResponseEntity<Void> deleteCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID) Long competitorId) throws BadRequestException {
		competitionService.deleteCompetitor(id, competitorId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update competitor from competition", notes = "Return updated competitor")
	public ResponseEntity<Competitor> putCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID) Long competitorId,
		@RequestBody @Valid Competitor competitor) throws BadRequestException {
		return new ResponseEntity<>(competitionService.updateCompetitor(id, competitorId, competitor), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competitor.", notes = "Return competitor")
	public ResponseEntity<Competitor> getCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID) Long competitorId) throws BadRequestException {
		return new ResponseEntity<>(competitionService.getCompetitor(id, competitorId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_WEAPON, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get weapon type lis", notes = "Return list of weapon type")
	public ResponseEntity<List<TypeWeapon>> getEnumWeapon () {
		return new ResponseEntity<>(competitionService.getListTypeWeapon(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_LIST_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added list competitors", notes = "Return list competitor")
	public ResponseEntity<List<Competitor>> postListCompetitors (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid List<Long> competitorsIdList) throws BadRequestException {
		return new ResponseEntity<>(competitionService.addedAllCompetitors(id, competitorsIdList), HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('JUDGE')")
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR_WITH_MARK, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added mark(rfid or number) and ready status for competitor", notes = "Updated competitor object")
	public ResponseEntity<Competitor> addedMarkForCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long competitionId,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID) Long competitorId, @RequestBody @Valid CompetitorMark competitorMark) throws BadRequestException {
		return new ResponseEntity<>(competitionService.addedMarkToCompetitor(competitionId, competitorId, competitorMark), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_LEVEL)
	@ApiOperation(value = "Return level description for competitors", notes = "Return list LevelBean object")
	public ResponseEntity<List<LevelBean>> getLevelEnum () {
		return new ResponseEntity<>(competitionService.getLevelEnum(), HttpStatus.OK);
	}
}
