package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.QuestionBean;
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.bean.QuizScoreBean;
import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.bean.RowBean;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.event.TestFinishedEvent;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.QuizScoreRepository;
import tech.shooting.ipsc.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuizService {
	
	@Autowired
	private QuizRepository quizRepository;
	
	@Autowired
	private QuizScoreRepository quizScoreRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private PersonRepository personRepository;
	
//	@Autowired
//	private WorkspaceService workspaceService;

	public Quiz createQuiz (QuizBean quiz) throws BadRequestException {
		Quiz quizToDB = new Quiz();
		BeanUtils.copyProperties(quiz, quizToDB, Quiz.SUBJECT);
		quizToDB.setSubject(checkSubject(quiz.getSubject()));
		return quizRepository.save(quizToDB);
	}

	private Subject checkSubject (Long subject) throws BadRequestException {
		return subjectRepository.findById(subject).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect subject id %s", subject)));
	}

	public List<Subject> getEnum () {
		return subjectRepository.findAll();
	}

	public List<Quiz> getAllQuiz () {
		return quizRepository.findAll();
	}

	public Quiz getQuiz (Long id) throws BadRequestException {
		return checkQuiz(id);
	}

	private Quiz checkQuiz (Long id) throws BadRequestException {
		return quizRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect quiz id %s", id)));
	}

	public Quiz updateQuiz (Long id, QuizBean quizBean) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		BeanUtils.copyProperties(quizBean, quiz);
		return quizRepository.save(quiz);
	}

	public void removeQuiz (Long id) throws BadRequestException {
		quizRepository.delete(checkQuiz(id));
	}

	public Question addQuestion (Long id, Question question) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		return quizRepository.pushQuestionToQuiz(quiz.getId(), question);
	}

	public Question getQuestion (Long id, Long questionId) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		return checkQuestion(quiz, questionId);
	}

	private Question checkQuestion (Quiz quiz, Long questionId) throws BadRequestException {
		return quiz.getQuestionList().stream().filter(ask -> ask.getId().equals(questionId)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect question id %s", questionId)));
	}

	public void deleteQuestion (Long id, Long questionId) {
		quizRepository.pullQuestion(id, questionId);
	}

	public Question updateQuestion (Long id, Long questionId, Question question) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		checkQuestion(quiz, questionId);
		quizRepository.pullQuestion(quiz.getId(), questionId);
		return quizRepository.pushQuestionToQuiz(quiz.getId(), question);
	}

	public List<Quiz> getQuizFromSubject (Long subject) {
		return quizRepository.findBySubject(subject);
	}

	public ResponseEntity getQuizByPage (Integer page, Integer size) {
		return Pageable.getPage(page, size, quizRepository);
	}

	public List<QuizScore> postScore (List<QuizScoreBean> listResult) throws BadRequestException {
		List<QuizScore> reports = new ArrayList<>();
		for(int i = 0; i < listResult.size(); i++) {
			reports.add(checkScore(listResult.get(i)));
		}
		return reports;
	}

	private QuizScore checkScore (QuizScoreBean reportBean) throws BadRequestException {
		//check quiz and person exist
		Quiz quiz = checkQuiz(reportBean.getQuizId());
		Person person = checkPerson(reportBean.getPerson());
		//get list question from quiz where status is active
		List<Question> collect = quiz.getQuestionList().stream().filter(Question :: isActive).collect(Collectors.toList());
		int questionCount = collect.size();
		int rightAnswers = 0;
		int incorrectAnswers = 0;
		int skippedAnswers = 0;
//		List<Row> incorrect = new ArrayList<>();
//		List<Ask> skip = new ArrayList<>();
		List<RowBean> list = reportBean.getList();
		for(int i = 0; i < list.size(); i++) {
			Question question = checkQuestion(quiz, list.get(i).getQuestionId());
			if(question.getRight() == list.get(i).getAnswer()) {
				rightAnswers++;
			} else {
//				Row row = new Row();
//				row.setAsk(question.getQuestion()).setAnswer(question.getAnswers().get(Math.toIntExact(list.get(i).getAnswer())));
//				incorrect.add(row);
				incorrectAnswers++;
			}
			collect.remove(question);
		}
		for(int i = 0; i < collect.size(); i++) {
//			skip.add(collect.get(i).getQuestion());
			skippedAnswers++;
		}
		double percentage = calculatePercentage(rightAnswers, questionCount);
		int score;
		if(percentage >= quiz.getGreat()) {
			score = 5;
		} else if(percentage >= quiz.getGood()) {
			score = 4;
		} else if(percentage >= quiz.getSatisfactorily()) {
			score = 3;
		} else {
			score = 2;
		}
		
		QuizScore report = new QuizScore().setQuizId(quiz.getId()).setPersonId(person.getId()).setScore(score).setCorrect(rightAnswers).setIncorrect(incorrectAnswers).setSkip(skippedAnswers).setTotal(questionCount);
		
		log.info("Quiz score is %s ", report);
		
//		EventBus.publishEvent(new TestFinishedEvent(workspace, score));
		
		return quizScoreRepository.save(report);
	}

	private Person checkPerson (Long person) throws BadRequestException {
		return personRepository.findById(person).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", person)));
	}

	private double calculatePercentage (double obtained, double total) {
		return (obtained / total) * 100;
	}

	public List<QuestionBean> getQuestionList (Long id) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		List<Question> collect = quiz.getQuestionList().stream().filter(Question :: isActive).collect(Collectors.toList());
		return convertToListQuestionsBean(collect);
	}

	private List<QuestionBean> convertToListQuestionsBean (List<Question> collect) {
		List<QuestionBean> res = new ArrayList<>();
		for(int i = 0; i < collect.size(); i++) {
			QuestionBean bean = new QuestionBean();
			BeanUtils.copyProperties(collect.get(i), bean);
			res.add(bean);
		}
		return res;
	}
	
	public List<QuizScore> getScoreList(QuizScoreRequest query) throws BadRequestException {
		if (query.getQuizId() != null) {
			checkQuiz(query.getQuizId());
		}
		if (query.getSubjectId() != null) {
			checkSubject(query.getSubjectId());
		}
		if (query.getPersonId() != null) {
			checkPerson(query.getPersonId());
		}

		return quizScoreRepository.getScoreList(query);
	}
}
