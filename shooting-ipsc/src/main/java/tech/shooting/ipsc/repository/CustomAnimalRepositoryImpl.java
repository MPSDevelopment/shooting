package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import tech.shooting.ipsc.pojo.Animal;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

public class CustomAnimalRepositoryImpl implements CustomAnimalRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Animal> findByPersonDivision(Division division) {
		
		Query query = new Query();
		if (division.getParent() == null) {
			return mongoTemplate.find(query, Animal.class);
		}
		
		Query personQuery = new Query();
		personQuery.addCriteria(Criteria.where("division").in(division.getAllChildren()));
		List<Person> persons = mongoTemplate.find(personQuery, Person.class);

		query.addCriteria(Criteria.where("owner").in(persons));
		return mongoTemplate.find(query, Animal.class);
	}
}
