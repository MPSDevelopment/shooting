package tech.shooting.ipsc.service;

import com.mpsdevelopment.plasticine.commons.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.enums.Subject;
import tech.shooting.ipsc.pojo.Question;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.SubjectsName;
import tech.shooting.ipsc.repository.QuizRepository;

import java.util.List;

@Service
@Slf4j
public class QuizService {
	@Autowired
	private QuizRepository quizRepository;

	public Quiz createQuiz (QuizBean quiz) {
		Quiz quizToDB = new Quiz();
		BeanUtils.copyProperties(quiz, quizToDB);
		return quizRepository.save(quizToDB);
	}

	public List<SubjectsName> getEnum () {
		return Subject.getList();
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
		if(question.getId() != null) {
			question.setId(IdGenerator.nextId());
		}
		List<Question> questionList = quiz.getQuestionList();
		questionList.add(question);
		quizRepository.save(quiz.setQuestionList(questionList));
		return question;
	}

	public Question getQuestion (Long id, Long questionId) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		return checkQuestion(quiz, questionId);
	}

	private Question checkQuestion (Quiz quiz, Long questionId) throws BadRequestException {
		return quiz.getQuestionList().stream().filter(ask -> ask.getId().equals(questionId)).findFirst().orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect question id %s", questionId)));
	}

	public void deleteQuestion (Long id, Long questionId) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		Question question = checkQuestion(quiz, questionId);
		List<Question> questionList = quiz.getQuestionList();
		questionList.remove(question);
		quizRepository.save(quiz.setQuestionList(questionList));
	}

	public Question updateQuestion (Long id, Long questionId, Question question) throws BadRequestException {
		Quiz quiz = checkQuiz(id);
		Question questionFromDB = checkQuestion(quiz, questionId);
		BeanUtils.copyProperties(question, questionFromDB);
		List<Question> questionList = quiz.getQuestionList();
		questionList.remove(question);
		questionList.add(questionFromDB);
		quizRepository.save(quiz.setQuestionList(questionList));
		return questionFromDB;
	}
}
