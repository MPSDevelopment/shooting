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
import tech.shooting.ipsc.pojo.Competitor;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Stage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = CompetitionRepository.class)
@ContextConfiguration(classes = {IpscMongoConfig.class})
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

	@Autowired
	private PersonRepository personRepository;

	@BeforeEach
	public void before () {
		competitionRepository.deleteAll();
		competition = new Competition().setName("Test name");
		stage = new Stage().setName("Test stage");
		competition.getStages().add(stage);
		competition = competitionRepository.save(competition);
		stage = competition.getStages().get(0);
		checkStagesId(competitionRepository.findById(competition.getId()).get());
	}
	
	@Test
	public void getAllActive () {
		var list = competitionRepository.findAllActive();
		assertEquals(1, list.size());
		
		competitionRepository.save(competition = new Competition().setName("Test new").setActive(true));
		list = competitionRepository.findAllActive();
		assertEquals(2, list.size());

		competitionRepository.save(competition.setActive(false));
		list = competitionRepository.findAllActive();
		assertEquals(1, list.size());
	}

	@Test
	public void checkFindByName () {
		String name = "Alladin";
		String location = "Cave777";
		competitionRepository.save(new Competition().setName(name).setLocation(location));
		assertNotNull(competitionRepository.findByName(name));
	}

	@Test
	public void checkGetByStageId () {
		Competition dbCompetition = competitionRepository.getByStageId(stage.getId());
		assertNotNull(dbCompetition);
		assertEquals(competition, dbCompetition);
		dbCompetition = competitionRepository.getByStageId(1L);
		assertNull(dbCompetition);
	}

	@Test
	public void checkGetStageById () {
		// var dbStage = competitionRepository.findByStageId(stage.getId());
		Stage dbStage = competitionRepository.getStageById(stage.getId());
		assertNotNull(dbStage);
		log.info("Got a stage %s", dbStage);
		assertEquals(stage.getId(), dbStage.getId());
		dbStage = competitionRepository.getStageById(1L);
		assertNull(dbStage);
	}

	@Test
	public void checkPushStage () {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// put another stage and check stage count and ids
		competitionRepository.pushStageToCompetition(competition.getId(), new Stage().setName("Test stage 2"));
		assertEquals(2, competitionRepository.findById(competition.getId()).get().getStages().size());
		checkStagesId(competitionRepository.findById(competition.getId()).get());
	}

	@Test
	public void checkPullStage () {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// pull stage and check stage count
		competitionRepository.pullStageFromCompetition(competition.getId(), stage);
		assertEquals(0, competitionRepository.findById(competition.getId()).get().getStages().size());
	}

	@Test
	public void checkPullStageId () {
		assertEquals(1, competitionRepository.findById(competition.getId()).get().getStages().size());
		// pull stage and check stage count
		competitionRepository.pullStageFromCompetition(competition.getId(), stage.getId());
		assertEquals(0, competitionRepository.findById(competition.getId()).get().getStages().size());
	}

	private void checkStagesId (Competition competition) {
		log.info("Competition id %s ", competition.getId());
		competition.getStages().forEach(item -> {
			log.info("Stage id %s and name %s", item.getId(), item.getName());
			assertNotNull(item.getId());
		});
	}

	@Test
	void checkPullCompetitorFromCompetition () {
		assertEquals(1, competitionRepository.findAll().size());
		Person pullTest = personRepository.save(new Person().setName("pullTest"));
		Competitor competitor = new Competitor().setPerson(pullTest).setName("pullTest").setActive(true);
		List<Competitor> competitors = competition.getCompetitors();
		competitors.add(competitor);
		competition = competitionRepository.save(competition.setCompetitors(competitors));
		competitor = competition.getCompetitors().get(0);
		competitionRepository.pullCompetitorFromCompetition(competition.getId(), competitor.getId());
		assertEquals(0, competitionRepository.findById(competition.getId()).get().getCompetitors().size());
	}
}
