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
import tech.shooting.ipsc.db.CompetitionDao;
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class, CompetitionDao.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class CompetitionDaoTest {

	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private CompetitionDao competitionDao;

	private Competition competition;

	private Stage stage;

	@BeforeEach
	public void before() {
		competitionRepository.deleteAll();

		competition = new Competition().setName("Test name");
		stage = new Stage().setNameOfStage("Test stage");
		competition.getStages().add(stage);

		competition = competitionRepository.save(competition);
		stage = competition.getStages().get(0);

		checkStagesId(competitionRepository.findById(competition.getId()).get());
	}
	
	@Test 
	public void checkGetStageById() {
		var dbStage = competitionRepository.findByStageId(stage.getId()); // competitionDao.getStageById(competition.getId(), stage.getId());
		assertNotNull(dbStage);
		log.info("Got a stage %s", dbStage);
//		assertEquals(stage.getId(), dbStage.getId());
	}

	@Test
	public void checkPushStage() {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// put another stage and check stage count and ids
		competitionDao.pushStageToCompetition(competition.getId(), new Stage().setNameOfStage("Test stage 2"));
		assertEquals(2, competitionRepository.findById(competition.getId()).get().getStages().size());
		checkStagesId(competitionRepository.findById(competition.getId()).get());
	}
	
	@Test
	public void checkPullStage() {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// pull stage and check stage count
		competitionDao.pullStageFromCompetition(competition.getId(), stage);
		assertEquals(0, competitionRepository.findById(competition.getId()).get().getStages().size());
	}

	private void checkStagesId(Competition competition) {
		log.info("Competition id %s ", competition.getId());
		competition.getStages().forEach(item -> {
			log.info("Stage id %s and name %s", item.getId(), item.getNameOfStage());
			assertNotNull(item.getId());
		});
	}

}
