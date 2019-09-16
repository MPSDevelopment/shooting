package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.bean.RatingBean;
import tech.shooting.ipsc.enums.DisqualificationEnum;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Competitor;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Score;

@ExtendWith(SpringExtension.class)
//@EnableMongoRepositories(basePackageClasses = CompetitionRepository.class)
//@ContextConfiguration(classes = { CompetitionService.class, IpscMongoConfig.class })
//@EnableAutoConfiguration
//@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class CompetitionServiceTest {

//	@Autowired
	public CompetitionService competitionService = new CompetitionService();

	private List<RatingBean> rating;

	@Test
	public void checkRating() {

		Competition competition = new Competition();
		addPerson(competition, 1L);
		addPerson(competition, 2L);
		addPerson(competition, 3L);
		addPerson(competition, 4L);

		rating = competitionService.convertScoresToRating(competition, new ArrayList<>());

		log.info("Rating is %s", JacksonUtils.getFullPrettyJson(rating));

		assertEquals(4, rating.size());

		List<Score> scores = new ArrayList<>();
		scores.add(new Score().setPersonId(1L).setStageId(1L).setScore(30).setTimeOfExercise(10));
		scores.add(new Score().setPersonId(1L).setStageId(1L).setScore(30).setTimeOfExercise(10));
		scores.add(new Score().setPersonId(3L).setStageId(3L).setScore(87).setTimeOfExercise(10));
		scores.add(new Score().setPersonId(1L).setStageId(2L).setScore(50).setTimeOfExercise(10));
		scores.add(new Score().setPersonId(1L).setStageId(3L).setScore(10).setTimeOfExercise(10).setDisqualificationReason(DisqualificationEnum.INJURED.toString()));
		scores.add(new Score().setPersonId(2L).setStageId(1L).setScore(10).setTimeOfExercise(10));
		scores.add(new Score().setPersonId(2L).setStageId(2L).setScore(50).setTimeOfExercise(10));

		rating = competitionService.convertScoresToRating(competition, competitionService.filterScores(scores));

		log.info("Rating is %s", JacksonUtils.getFullPrettyJson(rating));

		assertEquals(4, rating.size());
		assertEquals(8.7, rating.get(0).getHitFactor(), 0.1);
		assertEquals(100, rating.get(0).getPercentage());
		assertEquals(34.48, rating.get(1).getPercentage(), 0.1);
		assertEquals(3L, rating.get(0).getPersonId());
		assertEquals(1, rating.get(0).getStages());
		assertNull(rating.get(0).getDisqualification());
		assertEquals("1 " + DisqualificationEnum.INJURED.toString(), rating.get(1).getDisqualification());
		assertEquals(87, rating.get(0).getScore());
		assertEquals(3, rating.get(1).getStages());
		assertEquals(90, rating.get(1).getScore());
		assertEquals(0, rating.get(3).getScore());
		assertEquals(0, rating.get(3).getPercentage());

	}

	private void addPerson(Competition competition, Long id) {
		var person = new Person();
		person.setId(id);
		competition.getCompetitors().add(new Competitor().setPerson(person));
	}
}
