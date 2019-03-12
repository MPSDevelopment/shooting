package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.commons.pojo.Token;
import tech.shooting.ipsc.bean.CompetitionBean;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.repository.CompetitionRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.COMPETITION_CONTROLLER)
@Api(value = ControllerAPI.COMPETITION_CONTROLLER)
@Slf4j
public class CompetitionController {

	@Autowired
	private CompetitionRepository competitionRepository;


	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_CREATE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new Competition", notes = "Creates new Competition")
	public ResponseEntity<Competition> createCompetition (@RequestBody @Valid CompetitionBean competitionBean) throws BadRequestException {
		Competition competition = new Competition();
		BeanUtils.copyProperties(competitionBean, competition);
		createCompetition(competition);
		return new ResponseEntity<>(competition, HttpStatus.CREATED);
	}

	private void createCompetition (Competition competition) {
		log.info("Create competition with name %s ", competition.getName());
		if(competitionRepository.findByName(competition.getName()) != null) {
			throw new ValidationException(Competition.NAME, "Competition with name %s is already exist", competition.getName());
		}
		competition.setActive(true);
		if(competition.getStages() == null) {
			competition.setStages(new ArrayList<>());
		}
		competitionRepository.save(competition);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_BY_ID, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by id", notes = "Return competition object")
	public ResponseEntity<Competition> getCompetitionById (@PathVariable(value = "competitionId", required = true) Long id) throws BadRequestException {
		Competition competition = competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));

		return new ResponseEntity<>(competition, HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Remove competition", notes = "Return removed competition object")
	public ResponseEntity<Competition> deleteCompetitionById (@PathVariable(value = "competitionId", required = true) Long id) throws BadRequestException {
		Competition competition = competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
		competitionRepository.delete(competition);
		return new ResponseEntity<>(competition, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update competition", notes = "Return update competition object")
	public ResponseEntity<Competition> updateCompetition (@PathVariable(value = "competitionId", required = true) Long id, @RequestBody @Valid CompetitionBean competition) throws BadRequestException {
		Competition existCompetition = competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
		BeanUtils.copyProperties(competition, existCompetition);
		return new ResponseEntity<>(competitionRepository.save(existCompetition), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get count competitions", notes = "Return long count of competitions")
	public ResponseEntity<Long> getCount () throws BadRequestException {
		return new ResponseEntity<>(competitionRepository.count(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list competitions", notes = "Return List competition object")
	public ResponseEntity<List<Competition>> getAllCompetitions () throws BadRequestException {
		return new ResponseEntity<>(competitionRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITION_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = "page", description = "Current page number", response = String.class), @ResponseHeader(name = "total", description = "Total " +
		"records in database", response = String.class), @ResponseHeader(name = "pages", description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<Competition>> getCompetitionsByPage (@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @PathVariable(value = "pageNumber") Integer page,
	                                                                @PathVariable(value = "pageSize") Integer size) throws BadRequestException {
		return PageAble.getPage(page, size, competitionRepository);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stages from competition", notes = "Return list of stages object")
	public ResponseEntity<List<Stage>> getStagesFromCompetitionById (@PathVariable(value = "competitionId") Long id) throws BadRequestException {
		Competition competition = competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));

		return new ResponseEntity<>(competition.getStages(), HttpStatus.OK);
	}
}
