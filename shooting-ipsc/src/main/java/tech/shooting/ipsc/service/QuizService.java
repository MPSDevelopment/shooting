package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.bean.ReportBean;
import tech.shooting.ipsc.bean.RowBean;
import tech.shooting.ipsc.controller.PageAble;
import tech.shooting.ipsc.pojo.*;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.QuizRepository;
import tech.shooting.ipsc.repository.ReportRepository;
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
	private SubjectRepository subjectRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ReportRepository reportRepository;

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

	public void deleteQuestion (Long id, Long questionId) throws BadRequestException {
		quizRepository.pullQuestion(id, questionId);
	}

	public Question updateQuestion (Long id, Long questionId, Question question) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		checkQuestion(quiz, questionId);
		return quizRepository.pushQuestionToQuiz(quiz.getId(), question);
	}

	public List<Quiz> getQuizFromSubject (Long subject) {
		return quizRepository.findBySubject(subject);
	}

	public ResponseEntity getQuizByPage (Integer page, Integer size) {
		return PageAble.getPage(page, size, quizRepository);
	}

	public List<QuizReport> createReport (List<ReportBean> listResult) throws BadRequestException {
		List<QuizReport> reports = new ArrayList<>();
		for(int i = 0; i < listResult.size(); i++) {
			reports.add(checkReport(listResult.get(i)));
		}
		return reports;
	}

	private QuizReport checkReport (ReportBean reportBean) throws BadRequestException {
		//check quiz and person exist
		Quiz quiz = checkQuiz(reportBean.getQuizId());
		Person person = checkPerson(reportBean.getPerson());
		//get list question from quiz where status is active
		List<Question> collect = quiz.getQuestionList().stream().filter(que -> que.isActive() == true).collect(Collectors.toList());
		double countQuestion = collect.size();
		double rightAnswer = 0;
		List<Row> incorrect = new ArrayList<>();
		List<Ask> skip = new ArrayList<>();
		List<RowBean> list = reportBean.getList();
		for(int i = 0; i < list.size(); i++) {
			Question question = checkQuestion(quiz, list.get(i).getQuestionId());
			if(question.getRight() == list.get(i).getAnswer()) {
				rightAnswer++;
			} else {
				Row row = new Row();
				row.setAsk(question.getQuestion()).setAnswer(question.getAnswers().get(Math.toIntExact(list.get(i).getAnswer())));
				incorrect.add(row);
			}
			collect.remove(question);
		}
		for(int i = 0; i < collect.size(); i++) {
			skip.add(collect.get(i).getQuestion());
		}
		double mark = calculatePercentage(rightAnswer, countQuestion);
		int marki = 0;
		if(mark >= quiz.getGreat()) {
			marki = 5;
		} else if(mark >= quiz.getGood()) {
			marki = 4;
		} else if(mark >= quiz.getSatisfactorily()) {
			marki = 3;
		} else {
			marki = 2;
		}
		QuizReport save = reportRepository.save(new QuizReport().setQuiz(quiz).setPerson(person).setIncorrect(incorrect).setSkip(skip).setMark(marki));
		return save;
	}

	private Person checkPerson (Long person) throws BadRequestException {
		return personRepository.findById(person).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", person)));
	}

	public double calculatePercentage (double obtained, double total) {
		return (obtained / total) * 100;
	}
}
