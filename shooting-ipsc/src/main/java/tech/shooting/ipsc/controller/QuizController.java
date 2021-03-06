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
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.QuestionBean;
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.bean.QuizScoreBean;
import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.pojo.Question;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.QuizScore;
import tech.shooting.ipsc.pojo.Subject;
import tech.shooting.ipsc.service.QuizService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.QUIZ_CONTROLLER)
@Api(value = ControllerAPI.QUIZ_CONTROLLER)
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
public class QuizController {
	@Autowired
	private QuizService quizService;

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added new quiz", notes = "Return created quiz object")
	public ResponseEntity<Quiz> createQuiz(@RequestBody @Valid QuizBean quiz) throws BadRequestException {
		return new ResponseEntity<>(quizService.createQuiz(quiz), HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECTS_ENUM, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list of subjects", notes = "Return list of subjects")
	public ResponseEntity<List<Subject>> getEnumSubjects() {
		return new ResponseEntity<>(quizService.getEnum(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SUBJECT_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get list quiz from subject", notes = "Return list of quiz")
	public ResponseEntity<List<Quiz>> getQuizFromSubject(@PathVariable(value = ControllerAPI.PATH_VARIABLE_SUBJECT_ID) Long subject) {
		return new ResponseEntity<>(quizService.getQuizFromSubject(subject), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_ALL_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all quiz from db", notes = "Return list of exist quiz")
	public ResponseEntity<List<Quiz>> getAll() {
		return new ResponseEntity<>(quizService.getAllQuiz(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get quiz from db by id", notes = "Return quiz")
	public ResponseEntity<Quiz> getQuiz(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(quizService.getQuiz(id), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get quiz from db by id and return List Question", notes = "Return list question")
	public ResponseEntity<List<Question>> getQuizByIdAndReturnListQuestion(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(quizService.getQuiz(id).getQuestionList(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_LIST_QUESTION_TO_CHECK, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get question list to check", notes = "Return list question")
	public ResponseEntity<List<QuestionBean>> getQuizToCheck(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(quizService.getQuestionList(id), HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update quiz by id", notes = "Return updated  quiz")
	public ResponseEntity<Quiz> updateQuiz(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @RequestBody @Valid QuizBean quizBean) throws BadRequestException {
		return new ResponseEntity<>(quizService.updateQuiz(id, quizBean), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete quiz from db by id", notes = "Return status")
	public ResponseEntity<SuccessfulMessage> deleteQuiz(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id) throws BadRequestException {
		quizService.removeQuiz(id);
		return new ResponseEntity<>(new SuccessfulMessage("Quiz %d has been successfully deleted", id), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_QUESTION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Added question to quiz", notes = "Return question object")
	public ResponseEntity<Question> createQuestion(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @RequestBody @Valid Question question) throws BadRequestException {
		return new ResponseEntity<>(quizService.addQuestion(id, question), HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUESTION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get question", notes = "Return question object")
	public ResponseEntity<Question> getQuestion(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_QUESTION_ID) Long questionId) throws BadRequestException {
		return new ResponseEntity<>(quizService.getQuestion(id, questionId), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_DELETE_QUESTION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Delete  question", notes = "Return status")
	public ResponseEntity<SuccessfulMessage> deleteQuestion(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_QUESTION_ID) Long questionId) throws BadRequestException {
		quizService.deleteQuestion(id, questionId);
		return new ResponseEntity<>(new SuccessfulMessage("Question %d has been successfully deleted", id), HttpStatus.OK);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_PUT_QUESTION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update question to quiz", notes = "Return question object")
	public ResponseEntity<Question> updateQuestion(@PathVariable(value = ControllerAPI.PATH_VARIABLE_QUIZ_ID) Long id, @PathVariable(value = ControllerAPI.PATH_VARIABLE_QUESTION_ID) Long questionId, @RequestBody @Valid Question question)
			throws BadRequestException {
		return new ResponseEntity<>(quizService.updateQuestion(id, questionId, question), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_QUIZ_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get competition by page")
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", responseHeaders = { @ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class) }) })
	public ResponseEntity getQuizByPage(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return quizService.getQuizByPage(page, size);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_POST_ANSWER_TO_QUIZ, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Create row report's", notes = "Return list of reports")
	public ResponseEntity<List<QuizScore>> postScore(@RequestBody @Valid List<QuizScoreBean> listResult) throws BadRequestException {
		return new ResponseEntity<>(quizService.postScore(listResult), HttpStatus.CREATED);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get a score list for a query", notes = "Return score list")
	public ResponseEntity<List<QuizScore>> getScoreQueryList(@RequestBody QuizScoreRequest query) throws BadRequestException {
		return new ResponseEntity<>(quizService.getScoreList(query), HttpStatus.OK);
	}
	
	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.QUIZ_CONTROLLER_GET_SCORE_QUERY_LIST_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get quiz scores by page")
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", responseHeaders = { @ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class) }) })
	public ResponseEntity<List<QuizScore>> getScoreQueryListByPage(@RequestBody QuizScoreRequest query, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return quizService.getScoreList(query, page, size);
	}

}
