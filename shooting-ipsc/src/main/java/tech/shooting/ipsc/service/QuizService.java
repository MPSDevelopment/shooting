package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.QuizBean;
import tech.shooting.ipsc.enums.Subject;
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
}