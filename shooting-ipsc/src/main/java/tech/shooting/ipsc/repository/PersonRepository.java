package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends MongoRepository<Person, Long> {
	
	Person findByNameAndBirthDate (String name, OffsetDateTime birthDate);

	List<Person> findByDivision (Division division);
	
	List<Person> findByDivisionIn (List<Division> division);
	
	Page<Person> findByDivisionIn (List<Division> division, PageRequest pageable);
	
	Optional<Person> findByRfidCode(String rfidCode);
	
	Optional<Person> findByNumber(String number);
}
