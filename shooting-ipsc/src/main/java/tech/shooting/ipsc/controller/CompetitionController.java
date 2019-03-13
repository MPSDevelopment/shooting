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
import tech.shooting.ipsc.enums.ClassifierIPSC;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.repository.CompetitionRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(ControllerAPI.COMPETITION_CONTROLLER)
@Api(value = ControllerAPI.COMPETITION_CONTROLLER)
@Slf4j
public class CompetitionController {

	@Autowired
	private CompetitionRepository competitionRepository;

	private static final String PATH_VARIABLE_COMPETITION_ID = "competitionId";
	private static final String PATH_VARIABLE_STAGE_ID = "stageId";


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
	public ResponseEntity<Competition> getCompetitionById (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID, required = true) Long id) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(id);

		return new ResponseEntity<>(competition, HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_BY_ID, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Remove competition", notes = "Return removed competition object")
	public ResponseEntity<Competition> deleteCompetitionById (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID, required = true) Long id) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(id);
		competitionRepository.delete(competition);
		return new ResponseEntity<>(competition, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_BY_ID, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update competition", notes = "Return update competition object")
	public ResponseEntity<Competition> updateCompetition (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID, required = true) Long id, @RequestBody @Valid CompetitionBean competition) throws BadRequestException {
		Competition existCompetition = checkCompetitionsIfExist(id);
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
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = "page", description = "Current page number", response = String.class),
		@ResponseHeader(name = "total", description = "Total " + "records in database", response = String.class), @ResponseHeader(name = "pages", description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<Competition>> getCompetitionsByPage (@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @PathVariable(value = "pageNumber") Integer page,
		@PathVariable(value = "pageSize") Integer size) throws BadRequestException {
		return PageAble.getPage(page, size, competitionRepository);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stages from competition", notes = "Return list of stages object")
	public ResponseEntity<List<Stage>> getStagesFromCompetitionById (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID) Long id) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(id);

		return new ResponseEntity<>(competition.getStages(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added list stages to exist stages", notes = "Return list of stages")
	public ResponseEntity<List<Stage>> postStages (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid List<Stage> toAdded) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(id);

		competition.getStages().addAll(toAdded);
		return new ResponseEntity<>(competitionRepository.save(competition).getStages(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add stage to exist stages", notes = "Return created stage")
	public ResponseEntity<Stage> postStage (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid Stage toAdded) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(id);

		competition.getStages().add(toAdded);
		List<Stage> stages = competitionRepository.save(competition).getStages();
		int index = 0;
		for(int i = 0; i < stages.size(); i++) {
			if(stages.get(i).getNameOfStage().equals(toAdded.getNameOfStage()) && stages.get(i).getMaximumPoints().equals(toAdded.getMaximumPoints()) && stages.get(i).getTargets().equals(toAdded.getTargets())) {
				index = i;
			}
		}
		return new ResponseEntity<>(stages.get(index), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stage by id", notes = "Return stage object")
	public ResponseEntity<Stage> getStage (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID, required = true) Long competitionId, @PathVariable(value = PATH_VARIABLE_STAGE_ID, required = true) Long stageId) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(competitionId);
		return new ResponseEntity<>(checkStageIfExist(competition, stageId), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete stage by id", notes = "Return removed stage object")
	public ResponseEntity<Stage> deleteStage (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID, required = true) Long competitionId, @PathVariable(value = PATH_VARIABLE_STAGE_ID, required = true) Long stageId) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(competitionId);
		Stage stage = checkStageIfExist(competition, stageId);
		List<Stage> collect = competition.getStages().stream().filter((item) -> !item.getId().equals(stage.getId())).collect(Collectors.toList());

		competitionRepository.save(competition.setStages(collect));
		return new ResponseEntity<>(stage, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update stage ", notes = "Return updated stage object")
	public ResponseEntity<Stage> putStage (@PathVariable(value = PATH_VARIABLE_COMPETITION_ID, required = true) Long competitionId, @PathVariable(value = PATH_VARIABLE_STAGE_ID, required = true) Long stageId,
		@RequestBody @Valid Stage stage) throws BadRequestException {
		Competition competition = checkCompetitionsIfExist(competitionId);
		Stage stageFromDB = checkStageIfExist(competition, stageId);
		BeanUtils.copyProperties(stage, stageFromDB);

		List<Stage> stages = competition.getStages();

		stages.remove(stageFromDB);
		stages.add(stageFromDB);

		competitionRepository.save(competition.setStages(stages));

		return new ResponseEntity<>(stageFromDB, HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get const of ClassifierIPSC standard", notes = "Return list of ClassifierIPSC")
	public ResponseEntity<List<Stage>> getEnum () {

		return new ResponseEntity<>(ClassifierIPSC.getListStage(), HttpStatus.OK);
	}

	//Util method's
	private Competition checkCompetitionsIfExist (Long id) throws BadRequestException {
		return competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
	}

	private Stage checkStageIfExist (Competition competition, Long stageId) throws BadRequestException {
		return competition.getStages().stream().filter((i) -> i.getId().equals(stageId)).findAny().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect stageId %s", stageId)));
	}
}
