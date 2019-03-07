package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, Long> {

}
