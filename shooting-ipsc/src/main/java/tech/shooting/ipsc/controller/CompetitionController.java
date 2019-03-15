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
import tech.shooting.ipsc.enums.WeaponTypeEnum;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Competitor;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.pojo.TypeWeapon;
import tech.shooting.ipsc.repository.CompetitionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.UserRepository;

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

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new Competition", notes = "Creates new Competition")
	public ResponseEntity<Competition> createCompetition (@RequestBody @Valid CompetitionBean competitionBean) throws BadRequestException {
		Competition competition = useBeanUtilsWithOutJudges(competitionBean, new Competition());
		createCompetition(competition);
		return new ResponseEntity<>(competition, HttpStatus.CREATED);
	}

	private void createCompetition (Competition competition) {
		log.info("Create competition with name %s ", competition.getName());
		if(competitionRepository.findByName(competition.getName()) != null) {
			throw new ValidationException(Competition.NAME_FIELD, "Competition with name %s is already exist", competition.getName());
		}
		competition.setActive(true);
		if(competition.getStages() == null) {
			competition.setStages(new ArrayList<>());
		}
		competitionRepository.save(competition);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by id", notes = "Return competition object")
	public ResponseEntity<Competition> getCompetitionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		return new ResponseEntity<>(competition, HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Remove competition", notes = "Return removed competition object")
	public ResponseEntity<Competition> deleteCompetitionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competitionRepository.delete(competition);
		return new ResponseEntity<>(competition, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update competition", notes = "Return update competition object")
	public ResponseEntity<Competition> updateCompetition (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id, @RequestBody @Valid CompetitionBean competition) throws BadRequestException {
		Competition existCompetition = useBeanUtilsWithOutJudges(competition, checkCompetition(id));
		BeanUtils.copyProperties(competition, existCompetition);
		return new ResponseEntity<>(competitionRepository.save(existCompetition), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get count competitions", notes = "Return long count of competitions")
	public ResponseEntity<Long> getCount () throws BadRequestException {
		return new ResponseEntity<>(competitionRepository.count(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITIONS, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list competitions", notes = "Return List competition object")
	public ResponseEntity<List<Competition>> getAllCompetitions () throws BadRequestException {
		return new ResponseEntity<>(competitionRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITION_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = "page", description = "Current page number", response = String.class),
		@ResponseHeader(name = "total", description = "Total " + "records in database", response = String.class), @ResponseHeader(name = "pages", description = "Total pages in database", response = String.class)})})
	public ResponseEntity<List<Competition>> getCompetitionsByPage (@RequestHeader(value = Token.TOKEN_HEADER, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) throws BadRequestException {
		return PageAble.getPage(page, size, competitionRepository);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stages from competition", notes = "Return list of stages object")
	public ResponseEntity<List<Stage>> getStagesFromCompetitionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id) throws BadRequestException {
		Competition competition = checkCompetition(id);
		return new ResponseEntity<>(competition.getStages(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGES, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added list stages to exist stages", notes = "Return list of stages")
	public ResponseEntity<List<Stage>> postStages (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid List<Stage> toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competition.getStages().addAll(toAdded);
		return new ResponseEntity<>(competitionRepository.save(competition).getStages(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add stage to exist stages", notes = "Return created stage")
	public ResponseEntity<Stage> postStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID) Long id, @RequestBody @Valid Stage toAdded) throws BadRequestException {
		Competition competition = checkCompetition(id);
		competition.getStages().add(toAdded);
		List<Stage> stages = competitionRepository.save(competition).getStages();
		int index = 0;
		for(int i = 0; i < stages.size(); i++) {
			if(stages.get(i).getName().equals(toAdded.getName()) && stages.get(i).getMaximumPoints().equals(toAdded.getMaximumPoints()) && stages.get(i).getTargets().equals(toAdded.getTargets())) {
				index = i;
			}
		}
		return new ResponseEntity<>(stages.get(index), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get stage by id", notes = "Return stage object")
	public ResponseEntity<Stage> getStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long competitionId,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_STAGE_ID, required = true) Long stageId) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		return new ResponseEntity<>(checkStage(competition, stageId), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete stage by id", notes = "Return removed stage object")
	public ResponseEntity<Stage> deleteStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long competitionId,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_STAGE_ID, required = true) Long stageId) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		Stage stage = checkStage(competition, stageId);
		List<Stage> collect = competition.getStages().stream().filter((item) -> !item.getId().equals(stage.getId())).collect(Collectors.toList());
		competitionRepository.save(competition.setStages(collect));
		return new ResponseEntity<>(stage, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_STAGE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update stage ", notes = "Return updated stage object")
	public ResponseEntity<Stage> putStage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long competitionId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_STAGE_ID, required = true) Long stageId,
		@RequestBody @Valid Stage stage) throws BadRequestException {
		Competition competition = checkCompetition(competitionId);
		Stage stageFromDB = checkStage(competition, stageId);
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

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITORS, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list of competitors.", notes = "Return list competitors")
	public ResponseEntity<List<Competitor>> getCompetitors (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id) throws BadRequestException {
		return new ResponseEntity<>(checkCompetition(id).getCompetitors(), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added competitor to the list competitors", notes = "Return competitor object")
	public ResponseEntity<Competitor> postCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id, @RequestBody @Valid Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		checkPerson(competitor.getPerson().getId());
		Competitor competitorToDB = new Competitor();
		BeanUtils.copyProperties(competitor, competitorToDB);
		return new ResponseEntity<>(saveAndReturn(competition, competitorToDB, true), HttpStatus.CREATED);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_DELETE_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Remove competitor from competition", notes = "Return removed competitor")
	public ResponseEntity<Competitor> deleteCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID, required = true) Long competitorId) throws BadRequestException {
		Competition competition = checkCompetition(id);
		Competitor competitor = checkCompetitor(competition.getCompetitors(), competitorId);
		List<Competitor> competitors = competition.getCompetitors();
		competitors.remove(competitor);
		competition.setCompetitors(competitors);
		competitionRepository.save(competition);
		return new ResponseEntity<>(competitor, HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_PUT_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update competitor from competition", notes = "Return updated competitor")
	public ResponseEntity<Competitor> putCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID, required = true) Long competitorId, @RequestBody @Valid Competitor competitor) throws BadRequestException {
		Competition competition = checkCompetition(id);
		Competitor competitorFromDB = checkCompetitor(competition.getCompetitors(), competitorId);
		BeanUtils.copyProperties(competitor, competitorFromDB);
		return new ResponseEntity<>(saveAndReturn(competition, competitorFromDB, false), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COMPETITOR, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competitor.", notes = "Return competitor")
	public ResponseEntity<Competitor> getCompetitor (@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITION_ID, required = true) Long id,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_COMPETITOR_ID, required = true) Long competitorId) throws BadRequestException {
		return new ResponseEntity<>(checkCompetitor(checkCompetition(id).getCompetitors(), competitorId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_CONST_ENUM_WEAPON, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get weapon type lis", notes = "Return list of weapon type")
	public ResponseEntity<List<TypeWeapon>> getEnumWeapon () {
		return new ResponseEntity<>(WeaponTypeEnum.getList(), HttpStatus.OK);
	}

	//Util method's
	private Competitor checkCompetitor (List<Competitor> competitors, Long competitorId) throws BadRequestException {
		return competitors.stream().filter(competitor -> competitor.getId().equals(competitorId)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitor id $s", competitorId)));
	}

	private Competition checkCompetition (Long id) throws BadRequestException {
		return competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
	}

	private void checkPerson (Long id) throws BadRequestException {
		personRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitorId %s", id)));
	}

	private Stage checkStage (Competition competition, Long stageId) throws BadRequestException {
		return competition.getStages().stream().filter((i) -> i.getId().equals(stageId)).findAny().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect stageId %s", stageId)));
	}

	private Competitor saveAndReturn (Competition competition, Competitor competitorToDB, boolean flag) {
		List<Competitor> competitors = competition.getCompetitors();
		if(flag) {
			competitors.add(competitorToDB);
		} else {
			int indexF = 0;
			for(int i = 0; i < competitors.size(); i++) {
				if(competitors.get(i).getId().equals(competitorToDB.getId())) {
					indexF = i;
					break;
				}
			}
			competitors.set(indexF, competitorToDB);
		}
		competition.setCompetitors(competitors);
		competitors = competitionRepository.save(competition).getCompetitors();
		int index = 0;
		for(int i = 0; i < competitors.size(); i++) {
			if(competitors.get(i).getName().equals(competitorToDB.getName()) && competitors.get(i).getPerson().equals(competitorToDB.getPerson()) && competitors.get(i).getRfidCode().equals(competitorToDB.getRfidCode())) {
				index = i;
			}
		}
		return competitors.get(index);
	}

	private Competition useBeanUtilsWithOutJudges (CompetitionBean competitionBean, Competition competition) throws BadRequestException {
		BeanUtils.copyProperties(competitionBean, competition, Competition.MATCH_DIRECTOR_FIELD, Competition.RANGE_MASTER_FIELD, Competition.STATS_OFFICER_FIELD);
		if(competitionBean.getRangeMaster() != null) {
			competition.setRangeMaster(userRepository.findById(competitionBean.getRangeMaster()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Range Master id %s", competitionBean.getRangeMaster()))));
		}
		if(competitionBean.getMatchDirector() != null) {
			competition.setMatchDirector(userRepository.findById(competitionBean.getMatchDirector()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Match Director id %s", competitionBean.getMatchDirector()))));
		}
		if(competitionBean.getStatsOfficer() != null) {
			competition.setStatsOfficer(userRepository.findById(competitionBean.getStatsOfficer()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect Stats officer id %s", competitionBean.getStatsOfficer()))));
		}
		return competition;
	}
}
