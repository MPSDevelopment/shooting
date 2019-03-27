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
import tech.shooting.ipsc.pojo.Score;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = {IpscMongoConfig.class})
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class ScoreRepositoryTest {
	@Autowired
	private ScoreRepository scoreRepository;

	private Score score;

	private Score save;

	@BeforeEach
	public void before () {
		scoreRepository.deleteAll();
		score = new Score().setPersonId(11318797968798L).setStageId(67235467325467235L).setScore(50).setTimeOfExercise(535347263L);
	}

	@Test
	public void checkCreateEntity () {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
	}

	@Test
	public void checkFindAllByPersonId () {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
		score = new Score().setPersonId(113187979686798L).setStageId(67235467325467235L).setScore(40).setTimeOfExercise(5353478263L);
		save = scoreRepository.save(score);
		assertEquals(2, scoreRepository.count());
		assertEquals(1, scoreRepository.findAllByPersonId(11318797968798L).size());
	}

	@Test
	public void checkFindAllByStageId () {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
		score = new Score().setPersonId(113187979686798L).setStageId(6723534673325467235L).setScore(40).setTimeOfExercise(5353478263L);
		save = scoreRepository.save(score);
		assertEquals(2, scoreRepository.count());
		assertEquals(1, scoreRepository.findAllByStageId(67235467325467235L).size());
	}
}