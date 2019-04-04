package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

public class CustomSubjectRepositoryImpl implements CustomSubjectRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void createIfNotExists (List<Subject> subjects) {
		for(int i = 0; i < subjects.size(); i++) {
			Query query = Query.query(Criteria.where(Subject.KZ).is(subjects.get(i).getKz()));
			query.addCriteria(Criteria.where(Subject.RUS).is(subjects.get(i).getRus()));
			if(mongoTemplate.find(query, Subject.class).size() == 0 || mongoTemplate.find(query, Subject.class) == null) {
				mongoTemplate.save(subjects.get(i));
			}
		}
	}
}
