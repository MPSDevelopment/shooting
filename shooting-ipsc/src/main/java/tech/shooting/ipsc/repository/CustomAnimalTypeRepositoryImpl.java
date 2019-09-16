package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.ipsc.pojo.AnimalType;

@Slf4j
public class CustomAnimalTypeRepositoryImpl implements CustomAnimalTypeRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public AnimalType createIfNotExists(AnimalType type) {
		Query query = Query.query(Criteria.where(AnimalType.NAME).is(type.getName()));
		if (mongoTemplate.find(query, AnimalType.class) == null) {
			mongoTemplate.save(type);
		} else {
			log.info("Animal type %s already exists", type);
		}
		return type;
	}
}
