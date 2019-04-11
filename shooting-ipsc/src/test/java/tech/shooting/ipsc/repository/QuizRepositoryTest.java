package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class QuizRepositoryTest {
	@Autowired
	private QuizRepository quizRepository;

	private Quiz quiz;

	@Autowired
	private SubjectRepository subjectRepository;

	private List<Subject> subjects;

	private Subject subject;

	@BeforeEach
	public void before () {
		quizRepository.deleteAll();
		subjects = subjectRepository.findAll();
		if(subjects.size() == 0) {
			subject = subjectRepository.save(new Subject().setRus("fdfdfdfd").setKz("Fdfdfdfdfdf"));
		} else {
			subject = subjects.get(0);
		}
		quiz = new Quiz().setSubject(subject).setSatisfactorily(50).setName(new QuizName().setKz("test").setRus("да ну на"));
	}

	@Test
	public void checkFindBySubject () {
		quizRepository.save(quiz);
		quiz = new Quiz().setSubject(subjects.get(0)).setName(new QuizName().setRus("медведь").setKz("Audi"));
		quizRepository.save(quiz);
		quiz = new Quiz().setSubject(subjects.get(1)).setName(new QuizName().setKz("test").setRus("балалайка"));
		quizRepository.save(quiz);
		assertEquals(3, quizRepository.count());
		assertEquals(2, quizRepository.findBySubject(subjects.get(0)).size());
	}

	@Test
	public void checkFindBySubjectId () {
		quizRepository.save(quiz);
		quiz = new Quiz().setSubject(subjects.get(0)).setName(new QuizName().setRus("медведь").setKz("Audi"));
		quizRepository.save(quiz);
		quiz = new Quiz().setSubject(subjects.get(1)).setName(new QuizName().setKz("test").setRus("балалайка"));
		quizRepository.save(quiz);
		assertEquals(3, quizRepository.count());
		assertEquals(2, quizRepository.findBySubject(subjects.get(0).getId()).size());
	}

	@Test
	public void checkPullQuestion () {
		log.info("Count quiz in DB is %s", quizRepository.findAll().size());
		List<Question> list = new ArrayList<>();
		list.add(new Question().setQuestion(new Ask().setKz("fdfdfd").setRus("fdfdsfdsfsd"))
		                       .setActive(true)
		                       .setRight(3)
		                       .setRandom(true)
		                       .setAnswers(List.of(new Answer().setNumber(1).setKz("fdfdfd").setRus("fdfdfdfdfd"),
			                       new Answer().setNumber(3).setKz("fdfdfd").setRus("fdfdfdfdfd"),
			                       new Answer().setNumber(2).setKz("fdfdfd").setRus("fdfdfdfdfd"))));
		quiz.setQuestionList(list);
		Quiz save = quizRepository.save(quiz);
		Question question = save.getQuestionList().get(0);
		assertEquals(1, quizRepository.count());
		quizRepository.pullQuestion(quiz.getId(), question.getId());
		assertEquals(0, quizRepository.findById(save.getId()).get().getQuestionList().size());
	}

	@Test
	public void checkPushQuestionToQuiz () {
		log.info("Count quiz in DB is %s", quizRepository.findAll().size());
		List<Question> list = new ArrayList<>();
		list.add(new Question().setQuestion(new Ask().setKz("fdfdfd").setRus("fdfdsfdsfsd"))
		                       .setActive(true)
		                       .setRight(3)
		                       .setRandom(true)
		                       .setAnswers(List.of(new Answer().setNumber(1).setKz("fdfdfd").setRus("fdfdfdfdfd"),
			                       new Answer().setNumber(3).setKz("fdfdfd").setRus("fdfdfdfdfd"),
			                       new Answer().setNumber(2).setKz("fdfdfd").setRus("fdfdfdfdfd"))));
		quiz.setQuestionList(list);
		Quiz save = quizRepository.save(quiz);
		Question question = save.getQuestionList().get(0);
		question.setRight(1);
		assertEquals(1, quizRepository.count());
		quizRepository.pushQuestionToQuiz(quiz.getId(), question);
		assertEquals(2, quizRepository.findById(save.getId()).get().getQuestionList().size());
		assertEquals(1, quizRepository.findById(save.getId()).get().getQuestionList().get(1).getRight());
	}
}