package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.QuizScore;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomQuizScoreRepositoryImpl implements CustomQuizScoreRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DivisionRepository divisionRepository;

	@Override
	public List<QuizScore> getScoreList(QuizScoreRequest request) {

		Query query = new Query();
		if (request.getPersonId() != null) {
			query.addCriteria(Criteria.where(QuizScore.PERSON_FIELD).is(request.getPersonId()));
		} else {
			if (request.getDivisionId() != null) {

				var division = divisionRepository.findById(request.getDivisionId()).orElse(null);

				Query personQuery = new Query();
				personQuery.addCriteria(Criteria.where("division").in(division.getAllChildren()));
				List<Person> persons = mongoTemplate.find(personQuery, Person.class);

				log.info("There is %d persons for a division %s", persons.size(), division);

				query.addCriteria(Criteria.where(QuizScore.PERSON_FIELD).in(persons.stream().map(item -> {
					return item.getId();
				}).collect(Collectors.toList())));
			}
		}

		if (request.getQuizId() != null) {
			query.addCriteria(Criteria.where(QuizScore.QUIZ_FIELD).is(request.getQuizId()));
		} else {

			if (request.getSubjectId() != null && request.getQuizId() == null) {
				Query quizQuery = new Query();
				quizQuery.addCriteria(Criteria.where("subject.id").is(request.getSubjectId()));
				List<Quiz> quizs = mongoTemplate.find(quizQuery, Quiz.class);

				log.info("There is %d quizs for a subject %s", quizs.size(), request.getSubjectId());

				query.addCriteria(Criteria.where(QuizScore.QUIZ_FIELD).in(quizs.stream().map(item -> {
					return item.getId();
				}).collect(Collectors.toList())));
			}
		}

		if (request.getEndDate() != null && request.getStartDate() != null) {
			query.addCriteria(Criteria.where(QuizScore.TIME_FIELD).gte(request.getStartDate()).lte(request.getEndDate()));
		} else {

			if (request.getEndDate() != null) {
				query.addCriteria(Criteria.where(QuizScore.TIME_FIELD).lte(request.getEndDate()));
			}
			if (request.getStartDate() != null) {
				query.addCriteria(Criteria.where(QuizScore.TIME_FIELD).gte(request.getStartDate()));
			}
		}

		return mongoTemplate.find(query, QuizScore.class);
	}
}
