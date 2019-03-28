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
import tech.shooting.ipsc.enums.Subject;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.QuizName;

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

	@BeforeEach
	public void before () {
		quizRepository.deleteAll();
		quiz = new Quiz().setSubject(Subject.FIRE).setName(new QuizName().setKz("test").setRus("балалайка"));
	}

	@Test
	public void checkFindBySubject () {
		quizRepository.save(quiz);
		quiz = new Quiz().setSubject(Subject.FIRE).setName(new QuizName().setRus("медведь").setKz("Audi"));
		quizRepository.save(quiz);
		quiz = new Quiz().setSubject(Subject.COMMUNICATION).setName(new QuizName().setKz("test").setRus("балалайка"));
		quizRepository.save(quiz);
		assertEquals(3, quizRepository.count());
		assertEquals(2, quizRepository.findBySubject(Subject.FIRE).size());
	}
}