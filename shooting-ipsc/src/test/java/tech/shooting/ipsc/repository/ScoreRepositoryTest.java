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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.assertj.core.util.Arrays;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class ScoreRepositoryTest {
	
	private static final long FIRST_STAGE_ID = 6723534673325467235L;
	
	private static final long SECOND_STAGE_ID = 6723535464565467978L;
	
	private static final long PERSON_ID = 113187979686798L;
	
	private static final long ANOTHER_PERSON_ID = 672334553465467345L;

	@Autowired
	private ScoreRepository scoreRepository;

	private Score score;

	private Score save;

	@BeforeEach
	public void before() {
		scoreRepository.deleteAll();
		score = new Score().setPersonId(11318797968798L).setStageId(FIRST_STAGE_ID).setScore(50).setTimeOfExercise(535347263L);
	}

	@Test
	public void checkCreateEntity() {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
	}

	@Test
	public void checkFindAllByPersonId() {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
		score = new Score().setPersonId(PERSON_ID).setStageId(FIRST_STAGE_ID).setScore(40).setTimeOfExercise(5353478263L);
		save = scoreRepository.save(score);
		assertEquals(2, scoreRepository.count());
		assertEquals(1, scoreRepository.findAllByPersonId(11318797968798L).size());
	}

	@Test
	public void checkFindAllByStageId() {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
		score = new Score().setPersonId(PERSON_ID).setStageId(SECOND_STAGE_ID).setScore(40).setTimeOfExercise(5353478263L);
		save = scoreRepository.save(score);
		assertEquals(2, scoreRepository.count());
		assertEquals(1, scoreRepository.findAllByStageId(FIRST_STAGE_ID).size());
	}

	@Test
	public void checkfindByStageIdIn() {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
		score = new Score().setPersonId(PERSON_ID).setStageId(SECOND_STAGE_ID).setScore(40).setTimeOfExercise(5353478263L);
		save = scoreRepository.save(score);
		assertEquals(2, scoreRepository.count());

		var stages = new ArrayList<Long>();
		stages.add(FIRST_STAGE_ID);
		assertEquals(1, scoreRepository.findByStageIdIn(stages).size());
		stages.add(SECOND_STAGE_ID);
		assertEquals(2, scoreRepository.findByStageIdIn(stages).size());
		
		scoreRepository.save(new Score().setPersonId(ANOTHER_PERSON_ID).setStageId(SECOND_STAGE_ID).setScore(30).setTimeOfExercise(2L));
		
		assertEquals(3, scoreRepository.findByStageIdIn(stages).size());
	}

	@Test
	public void checkFindByPersonIdAndStageId() {
		assertEquals(0, scoreRepository.count());
		save = scoreRepository.save(score);
		assertEquals(1, scoreRepository.count());
		score = new Score().setPersonId(PERSON_ID).setStageId(FIRST_STAGE_ID).setScore(40).setTimeOfExercise(5353478263L);
		save = scoreRepository.save(score);
		assertEquals(2, scoreRepository.count());
		assertNotNull(scoreRepository.findByPersonIdAndStageId(113187979686798L, FIRST_STAGE_ID));
	}
}