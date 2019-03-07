package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.shooting.ipsc.pojo.Person;

public interface PersonRepository extends MongoRepository<Person, Long> {

}
