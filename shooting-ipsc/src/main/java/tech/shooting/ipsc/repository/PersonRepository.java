package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Person;

import java.time.OffsetDateTime;

@Repository
public interface PersonRepository extends MongoRepository<Person, Long> {
	
	Person findByNameAndBirthDate (String name, OffsetDateTime birthDate);

}
