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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(ControllerAPI.COMPETITION_CONTROLLER)
@Api(value = ControllerAPI.COMPETITION_CONTROLLER)
@Slf4j
public class CompetitionController {

	@Autowired
	private CompetitionRepository competitionRepository;


	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COMPETITION_CONTROLLER_POST_CREATE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new Competition", notes = "Creates new Competition")
	public ResponseEntity<Competition> createCompetition (HttpServletRequest request, @RequestBody @Valid CreateCompetition createCompetition) throws BadRequestException {
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
	public ResponseEntity<Competition> getCompetitionById (@PathVariable(value = "competitionId", required = true) Long id) throws BadRequestException {
		Competition competition = competitionRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Not find competition with %s id", id)));

		return new ResponseEntity<>(competition, HttpStatus.OK);
	}
}
