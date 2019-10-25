package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.pojo.StandardScore;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomStandardScoreRepositoryImpl implements CustomStandardScoreRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DivisionRepository divisionRepository;

	@Override
	public List<StandardScore> getScoreList(StandardScoreRequest request) {
		Query query = getQuery(request);
		return mongoTemplate.find(query, StandardScore.class);
	}
	
	@Override
	public Page<StandardScore> getScoreList(StandardScoreRequest request, PageRequest pageable) {
		return PageableExecutionUtils.getPage(mongoTemplate.find(getQuery(request).with(pageable), StandardScore.class), pageable, () -> mongoTemplate.count(getQuery(request), StandardScore.class));
	}

	private Query getQuery(StandardScoreRequest request) {
		Query query = new Query();
		if (request.getPersonId() != null) {
			query.addCriteria(Criteria.where(StandardScore.PERSON_FIELD).is(request.getPersonId()));
		} else if (request.getDivisionId() != null) {

			var division = divisionRepository.findById(request.getDivisionId()).orElse(null);

			Query personQuery = new Query();
			personQuery.addCriteria(Criteria.where("division").in(division.getAllChildren()));
			List<Person> persons = mongoTemplate.find(personQuery, Person.class);

			log.info("There is %d persons for a division %s", persons.size(), division);

			query.addCriteria(Criteria.where(StandardScore.PERSON_FIELD).in(persons.stream().map(item -> {
				return item.getId();
			}).collect(Collectors.toList())));
		}

		if (request.getStandardId() != null) {
			query.addCriteria(Criteria.where(StandardScore.STANDARD_FIELD).is(request.getStandardId()));
		} else if (request.getSubjectId() != null && request.getStandardId() == null) {
			Query standardQuery = new Query();
			standardQuery.addCriteria(Criteria.where("subject.id").is(request.getSubjectId()));
			List<Standard> standards = mongoTemplate.find(standardQuery, Standard.class);

			log.info("There is %d standards for a subject %s", standards.size(), request.getSubjectId());

			query.addCriteria(Criteria.where(StandardScore.STANDARD_FIELD).in(standards.stream().map(item -> {
				return item.getId();
			}).collect(Collectors.toList())));
		}

		if (request.getEndDate() != null && request.getStartDate() != null) {
			query.addCriteria(Criteria.where(StandardScore.TIME_FIELD).gte(request.getStartDate()).lte(request.getEndDate()));
		} else {

			if (request.getEndDate() != null) {
				query.addCriteria(Criteria.where(StandardScore.TIME_FIELD).lte(request.getEndDate()));
			}
			if (request.getStartDate() != null) {
				query.addCriteria(Criteria.where(StandardScore.TIME_FIELD).gte(request.getStartDate()));
			}
		}
		return query;
	}
}
