package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import tech.shooting.ipsc.pojo.CommunicationEquipment;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

public class CustomCommunicationEquipmentRepositoryImpl implements CustomCommunicationEquipmentRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<CommunicationEquipment> findByPersonDivision(Division division) {
		
		Query query = new Query();
		if (division.getParent() == null) {
			return mongoTemplate.find(query, CommunicationEquipment.class);
		}
		
		Query personQuery = new Query();
		personQuery.addCriteria(Criteria.where("division").in(division.getAllChildren()));
		List<Person> persons = mongoTemplate.find(personQuery, Person.class);

		query.addCriteria(Criteria.where("owner").in(persons));
		return mongoTemplate.find(query, CommunicationEquipment.class);
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
