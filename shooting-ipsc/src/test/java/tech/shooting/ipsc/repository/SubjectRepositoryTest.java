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
import tech.shooting.ipsc.pojo.Subject;

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
class SubjectRepositoryTest {
	@Autowired
	private SubjectRepository subjectRepository;

	private List<Subject> subjects;

	private Subject subject;

	@BeforeEach
	public void before () {
		subjects = subjectRepository.findAll();
	}

	@Test
	public void checkCreateIfNotExists () {
		log.info("List of subject's size %s", subjects.size());
		if(subjects.size() == 0) {
			subject = subjectRepository.save(new Subject().setKz("fdfdfd").setRus("kjdfkujdshf"));
		} else {
			subject = subjects.get(0);
		}
		log.info("My test subject is %s", subject);
		//create list
		List<Subject> testList = new ArrayList<>();
		testList.add(subject);
		List<Subject> ifNotExists = subjectRepository.createIfNotExists(testList);
		assertEquals(ifNotExists.size(), 0);
	}
}