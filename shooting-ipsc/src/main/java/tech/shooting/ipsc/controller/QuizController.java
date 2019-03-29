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
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.SubjectsName;
import tech.shooting.ipsc.service.QuizService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.QUIZ_CONTROLLER)
@Api(value = ControllerAPI.QUIZ_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class QuizController {
	@Autowired
	private QuizService quizService;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added new quiz", notes = "Return created quiz object")
	public ResponseEntity<Quiz> createQuiz (@RequestBody @Valid QuizBean quiz) {
		return new ResponseEntity<>(quizService.createQuiz(quiz), HttpStatus.CREATED);
	}

	@GetMapping(ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM)
	@ApiOperation(value = "Get list of subjects", notes = "Return list of subjects")
	public ResponseEntity<List<SubjectsName>> getEnumSubjects () {
		return new ResponseEntity<>(quizService.getEnum(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ)
	@ApiOperation(value = "Get all quiz from db", notes = "Return list of exist quiz")
	public ResponseEntity<List<Quiz>> getAll () {
		return new ResponseEntity<>(quizService.getAllQuiz(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ)
	@ApiOperation(value = "Get quiz from db by id", notes = "Return quiz")
	public ResponseEntity<Quiz> getQuiz (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(quizService.getQuiz(id), HttpStatus.OK);
	}

}