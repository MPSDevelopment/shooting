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
import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Info;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Score;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.pojo.StandardScore;
import tech.shooting.ipsc.pojo.Subject;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
class StandardScoreRepositoryTest {

	@Autowired
	private StandardScoreRepository scoreRepository;

	@Autowired
	private StandardRepository standardRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private SubjectRepository subjectRepository;
	
	@Autowired
	private DivisionRepository divisionRepository;

	private Standard testStandard;

	private Person anotherPerson;

	private Person testingPerson;

	private Subject testSubject;

	private Subject anotherSubject;

	private Standard anotherStandard;

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

		testStandard = standardRepository.save(new Standard().setActive(true).setSubject(testSubject).setGroups(false));
		anotherStandard = standardRepository.save(new Standard().setActive(true).setSubject(anotherSubject).setGroups(false));
		
		root = divisionRepository.save(new Division().setName("Root").setParent(null));
		division = divisionRepository.save(new Division().setName("Division").setParent(root));
		childDivision = divisionRepository.save(new Division().setName("Child").setParent(division));
		anotherDivision = divisionRepository.save(new Division().setName("Another").setParent(root));
		
		divisionRepository.save(root);
		divisionRepository.save(division);

		testingPerson = personRepository.save(new Person().setName("testing person").setDivision(childDivision));
		anotherPerson = personRepository.save(new Person().setName("another person").setDivision(anotherDivision));

		scoreRepository.save(new StandardScore().setDatetime(now).setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(4).setTimeOfExercise(23));
		scoreRepository.save(new StandardScore().setDatetime(now.plusMinutes(2)).setPersonId(testingPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));
		scoreRepository.save(new StandardScore().setDatetime(now.plusMinutes(4)).setPersonId(anotherPerson.getId()).setStandardId(testStandard.getId()).setScore(3).setTimeOfExercise(27));

		scoreRepository.save(new StandardScore().setDatetime(now.plusMinutes(6)).setPersonId(anotherPerson.getId()).setStandardId(anotherStandard.getId()).setScore(3).setTimeOfExercise(27));
	}

	@Test
	public void checkGetScoreList() {

		assertEquals(4, scoreRepository.getScoreList(new StandardScoreRequest()).size());

		// only person
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setPersonId(testingPerson.getId())).size());
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setPersonId(anotherPerson.getId())).size());
		
		// only division
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setDivisionId(childDivision.getId())).size());
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setDivisionId(anotherDivision.getId())).size());
		assertEquals(4, scoreRepository.getScoreList(new StandardScoreRequest().setDivisionId(root.getId())).size());

		// only standard
		assertEquals(3, scoreRepository.getScoreList(new StandardScoreRequest().setStandardId(testStandard.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new StandardScoreRequest().setStandardId(anotherStandard.getId())).size());

		// only subject
		assertEquals(3, scoreRepository.getScoreList(new StandardScoreRequest().setSubjectId(testSubject.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new StandardScoreRequest().setSubjectId(anotherSubject.getId())).size());

		// person and standard
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setPersonId(testingPerson.getId()).setStandardId(testStandard.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new StandardScoreRequest().setPersonId(anotherPerson.getId()).setStandardId(testStandard.getId())).size());

		// person and standard and time 
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setStartDate(now.minusMinutes(1)).setEndDate(now.plusMinutes(10)).setPersonId(testingPerson.getId()).setStandardId(testStandard.getId())).size());
		assertEquals(1, scoreRepository.getScoreList(new StandardScoreRequest().setStartDate(now.minusMinutes(1)).setEndDate(now.plusMinutes(1)).setPersonId(testingPerson.getId()).setStandardId(testStandard.getId())).size());
		assertEquals(2, scoreRepository.getScoreList(new StandardScoreRequest().setStartDate(now.minusMinutes(1)).setEndDate(now.plusMinutes(3)).setPersonId(testingPerson.getId()).setStandardId(testStandard.getId())).size());
	}
}