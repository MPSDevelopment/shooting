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
import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class CompetitionRepositoryTest {

	@Autowired
	private CompetitionRepository competitionRepository;

	private Competition competition;

	private Stage stage;

	@BeforeEach
	public void before() {
		competitionRepository.deleteAll();

		competition = new Competition().setName("Test name");
		stage = new Stage().setName("Test stage");
		competition.getStages().add(stage);
		competition = competitionRepository.save(competition);
		stage = competition.getStages().get(0);

		checkStagesId(competitionRepository.findById(competition.getId()).get());
	}

	@Test
	public void checkFindByName() {
		String name = "Alladin";
		String location = "Cave777";

		competitionRepository.save(new Competition().setName(name).setLocation(location));
		assertNotNull(competitionRepository.findByName(name));
	}

	@Test
	public void checkGetByStageId() {
		var dbCompetition = competitionRepository.getByStageId(stage.getId());
		assertNotNull(dbCompetition);
		assertEquals(competition, dbCompetition);
		dbCompetition = competitionRepository.getByStageId(1L);
		assertNull(dbCompetition);
	}

	@Test
	public void checkGetStageById() {
		// var dbStage = competitionRepository.findByStageId(stage.getId());
		var dbStage = competitionRepository.getStageById(stage.getId());
		assertNotNull(dbStage);
		log.info("Got a stage %s", dbStage);
		assertEquals(stage.getId(), dbStage.getId());

		dbStage = competitionRepository.getStageById(1L);
		assertNull(dbStage);
	}

	@Test
	public void checkPushStage() {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// put another stage and check stage count and ids
		competitionRepository.pushStageToCompetition(competition.getId(), new Stage().setName("Test stage 2"));
		assertEquals(2, competitionRepository.findById(competition.getId()).get().getStages().size());
		checkStagesId(competitionRepository.findById(competition.getId()).get());
	}

	@Test
	public void checkPullStage() {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// pull stage and check stage count
		competitionRepository.pullStageFromCompetition(competition.getId(), stage);
		assertEquals(0, competitionRepository.findById(competition.getId()).get().getStages().size());
	}

	private void checkStagesId(Competition competition) {
		log.info("Competition id %s ", competition.getId());
		competition.getStages().forEach(item -> {
			log.info("Stage id %s and name %s", item.getId(), item.getName());
			assertNotNull(item.getId());
		});
	}

}
