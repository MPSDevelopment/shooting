package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.QuizScore;
import tech.shooting.ipsc.pojo.Subject;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class QuizScoreRepositoryTest {

	@Autowired
	private QuizScoreRepository scoreRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	private Quiz testQuiz;

	private Person anotherPerson;

	private Person testingPerson;

	private Subject testSubject;

	private Subject anotherSubject;

	private Quiz anotherQuiz;

	private OffsetDateTime now;

	private Division root;

	private Division division;

	private Division childDivision;

	private Division anotherDivision;

	@BeforeEach
	public void before() {
		scoreRepository.deleteAll();

		now = OffsetDateTime.now();

		testSubject = subjectRepository.save(new Subject().setRus("First").setKz("First"));
		anotherSubject = subjectRepository.save(new Subject().setRus("Second").setKz("Second"));

		testQuiz = quizRepository.save(new Quiz().setActive(true).setSubject(testSubject));
		anotherQuiz = quizRepository.save(new Quiz().setActive(true).setSubject(anotherSubject));

		root = divisionRepository.save(new Division().setName("Root").setParent(null));
		division = divisionRepository.save(new Division().setName("Division").setParent(root));
		childDivision = divisionRepository.save(new Division().setName("Child").setParent(division));
		anotherDivision = divisionRepository.save(new Division().setName("Another").setParent(root));

		divisionRepository.save(root);
		divisionRepository.save(division);

		testingPerson = personRepository.save(new Person().setName("testing person").setDivision(childDivision));
		anotherPerson = personRepository.save(new Person().setName("another person").setDivision(anotherDivision));

		scoreRepository.save(new QuizScore().setDatetime(now).setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(4));
		scoreRepository.save(new QuizScore().setDatetime(now.plusMinutes(2)).setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(3));
		scoreRepository.save(new QuizScore().setDatetime(now.plusMinutes(4)).setPerson(anotherPerson).setQuizId(testQuiz.getId()).setScore(3));

		scoreRepository.save(new QuizScore().setDatetime(now.plusMinutes(6)).setPerson(anotherPerson).setQuizId(anotherQuiz.getId()).setScore(3));
	}

	@Test
	public void checkGetScoreList() {

		assertEquals(4, scoreRepository.getScoreList(new QuizScoreRequest()).size());

		// only person
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setPersonId(testingPerson.getId())).size());
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setPersonId(anotherPerson.getId())).size());

		// only division
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setDivisionId(childDivision.getId())).size());
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setDivisionId(anotherDivision.getId())).size());
		assertEquals(4, scoreRepository.getScoreList(new QuizScoreRequest().setDivisionId(root.getId())).size());

		// only standard
		assertEquals(3, scoreRepository.getScoreList(new QuizScoreRequest().setQuizId(testQuiz.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new QuizScoreRequest().setQuizId(anotherQuiz.getId())).size());

		// only subject
		assertEquals(3, scoreRepository.getScoreList(new QuizScoreRequest().setSubjectId(testSubject.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new QuizScoreRequest().setSubjectId(anotherSubject.getId())).size());

		// person and standard
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setPersonId(testingPerson.getId()).setQuizId(testQuiz.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new QuizScoreRequest().setPersonId(anotherPerson.getId()).setQuizId(testQuiz.getId())).size());

		// person and standard and time
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setStartDate(now.minusMinutes(1)).setEndDate(now.plusMinutes(10)).setPersonId(testingPerson.getId()).setQuizId(testQuiz.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new QuizScoreRequest().setStartDate(now.minusMinutes(1)).setEndDate(now.plusMinutes(1)).setPersonId(testingPerson.getId()).setQuizId(testQuiz.getId())).size());
		assertEquals(2, scoreRepository.getScoreList(new QuizScoreRequest().setStartDate(now.minusMinutes(1)).setEndDate(now.plusMinutes(3)).setPersonId(testingPerson.getId()).setQuizId(testQuiz.getId())).size());
	}

	@Test
	public void checkGetScoreListPaging() {
		for (int i = 0; i < 40; i++) {
			scoreRepository.save(new QuizScore().setDatetime(now.plusHours(1)).setPerson(testingPerson).setQuizId(testQuiz.getId()).setScore(i / 10 + 1));
		}
		PageRequest pageable = PageRequest.of(1, 10, Sort.Direction.ASC, QuizScore.TIME_FIELD);
		Page<QuizScore> page = scoreRepository.getScoreList(new QuizScoreRequest(), pageable);

		assertEquals(1, page.getNumber());
		assertEquals(10, page.getNumberOfElements());
		assertEquals(scoreRepository.count(), page.getTotalElements());
		assertEquals(5, page.getTotalPages());
	}
}