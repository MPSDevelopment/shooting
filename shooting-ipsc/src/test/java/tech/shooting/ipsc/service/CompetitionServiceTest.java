package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
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
import tech.shooting.commons.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.bean.RatingBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Score;
import tech.shooting.ipsc.pojo.Stage;
import tech.shooting.ipsc.repository.CompetitionRepository;

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

	@Test
	public void checkRating() {
		
		List<Score> scores = new ArrayList<>();
		scores.add(new Score().setPersonId(1L).setStageId(1L).setScore(10));
		scores.add(new Score().setPersonId(3L).setStageId(3L).setScore(1));
		scores.add(new Score().setPersonId(1L).setStageId(2L).setScore(5));
		scores.add(new Score().setPersonId(1L).setStageId(3L).setScore(1));
		scores.add(new Score().setPersonId(2L).setStageId(1L).setScore(10));
		scores.add(new Score().setPersonId(2L).setStageId(2L).setScore(5));
		
		var rating = competitionService.convertScoresToRating(scores);
		
		log.info("Rating is %s", JacksonUtils.getFullPrettyJson(rating));
		
		assertEquals(3, rating.size());
		
	}
}