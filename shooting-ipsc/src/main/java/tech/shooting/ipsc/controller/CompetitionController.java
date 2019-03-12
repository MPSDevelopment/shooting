package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import tech.shooting.ipsc.bean.CreateCompetition;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.repository.CompetitionRepository;

import javax.validation.Valid;
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
	public ResponseEntity<Competition> createCompetition (@RequestBody @Valid CreateCompetition createCompetition) throws BadRequestException {
		Competition competition = new Competition();
		BeanUtils.copyProperties(createCompetition, competition);
		createPerson(competition);
		return new ResponseEntity<>(competition, HttpStatus.CREATED);
	}

	private void createPerson (Competition competition) {
		log.info("Create competition with name %s ", competition.getName());
		if(competitionRepository.findByName(competition.getName()) != null) {
			throw new ValidationException(Competition.NAME, "Competition with name %s is already exist", competition.getName());
		}
		competition.setActive(true);
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
	public ResponseEntity<Competition> updateCompetition (@PathVariable(value = "competitionId", required = true) Long id, @RequestBody @Valid CreateCompetition competition) throws BadRequestException {
		Competition existCompetition = competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect competitionId %s", id)));
		BeanUtils.copyProperties(competition, existCompetition);
		return new ResponseEntity<>(competitionRepository.save(existCompetition), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_COUNT)
	@ApiOperation(value = "Get count competitions", notes = "Return long count of competitions")
	public ResponseEntity<Long> getCount () throws BadRequestException {
		return new ResponseEntity<>(competitionRepository.count(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_GET_ALL_COMPETITIONS)
	@ApiOperation(value = "Get list competitions", notes = "Return List competition object")
	public ResponseEntity<List<Competition>> getAllCompetitions () throws BadRequestException {
		return new ResponseEntity<>(competitionRepository.findAll(), HttpStatus.OK);
	}
}
