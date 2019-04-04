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
import tech.shooting.ipsc.pojo.Question;
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

	@GetMapping(ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ)
	@ApiOperation(value = "Get list quiz from subject", notes = "Return list of quiz")
	public ResponseEntity<List<Quiz>> getQuizFromSubject (@PathVariable(value = ControllerAPI.PATH_VARIABLE_SUBJECT) String subject) {
		return new ResponseEntity<>(quizService.getQuizFromSubject(subject), HttpStatus.OK);
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

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update quiz by id", notes = "Return updated  quiz")
	public ResponseEntity<Quiz> updateQuiz (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @RequestBody @Valid QuizBean quizBean) throws BadRequestException {
		return new ResponseEntity<>(quizService.updateQuiz(id, quizBean), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ)
	@ApiOperation(value = "Delete quiz from db by id", notes = "Return status")
	public ResponseEntity deleteQuiz (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id) throws BadRequestException {
		quizService.removeQuiz(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added question to quiz", notes = "Return question object")
	public ResponseEntity<Question> createQuestion (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @RequestBody @Valid Question question) throws BadRequestException {
		return new ResponseEntity<>(quizService.addQuestion(id, question), HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION)
	@ApiOperation(value = "Get question", notes = "Return question object")
	public ResponseEntity<Question> getQuestion (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id,
		@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUESTION_ID) Long questionId) throws BadRequestException {
		return new ResponseEntity<>(quizService.getQuestion(id, questionId), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION)
	@ApiOperation(value = "Delete  question", notes = "Return status")
	public ResponseEntity deleteQuestion (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_QUESTION_ID) Long questionId) throws BadRequestException {
		quizService.deleteQuestion(id, questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update question to quiz", notes = "Return question object")
	public ResponseEntity<Question> updateQuestion (@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_QUESTION_ID) Long questionId,
		@RequestBody @Valid Question question) throws BadRequestException {
		return new ResponseEntity<>(quizService.updateQuestion(id, questionId, question), HttpStatus.OK);
	}

}
