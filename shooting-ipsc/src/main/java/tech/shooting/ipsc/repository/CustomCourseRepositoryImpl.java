package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

public class CustomCourseRepositoryImpl implements CustomCourseRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Course> findByPersonDivisionIn(Division division) {

		Query query = new Query();
		if (division.getParent() == null) {
			return mongoTemplate.find(query, Course.class);
		}

		Query personQuery = new Query();
		personQuery.addCriteria(Criteria.where("division").in(division.getAllChildren()));
		var persons = mongoTemplate.find(personQuery, Person.class);

		query.addCriteria(Criteria.where("person").in(persons));
		return mongoTemplate.find(query, Course.class);
	}

	@Override
	public Page<Course> findByPersonDivisionIn(Division division, PageRequest pageable) {

		Query query = new Query().with(pageable);
		if (division.getParent() == null) {
			var list = mongoTemplate.find(query, Course.class);
			return PageableExecutionUtils.getPage(list, pageable, () -> mongoTemplate.count(query, Course.class));
		}

		Query personQuery = new Query();
		personQuery.addCriteria(Criteria.where("division").in(division.getAllChildren()));
		var persons = mongoTemplate.find(personQuery, Person.class);

		query.addCriteria(Criteria.where("person").in(persons));
		var list = mongoTemplate.find(query, Course.class);

		return PageableExecutionUtils.getPage(list, pageable, () -> mongoTemplate.count(query, Course.class));
	}

//	@Override
//	public List<Subject> createIfNotExists (List<Subject> subjects) {
//		List<Subject> res = new ArrayList<>();
//		for(int i = 0; i < subjects.size(); i++) {
//			Query query = Query.query(Criteria.where(Subject.KZ).is(subjects.get(i).getKz()));
//			query.addCriteria(Criteria.where(Subject.RUS).is(subjects.get(i).getRus()));
//			if(mongoTemplate.find(query, Subject.class).size() == 0 || mongoTemplate.find(query, Subject.class) == null) {
//				res.add(mongoTemplate.save(subjects.get(i)));
//			}
//		}
//		return res;
//	}
}
