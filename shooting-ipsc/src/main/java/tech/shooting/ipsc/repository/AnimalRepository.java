package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Animal;
import tech.shooting.ipsc.pojo.AnimalType;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, Long> {

	Animal findByName(String serial);

	List<Animal> findByOwner(Person person);

	List<Animal> findByOwnerIn(List<Person> list);

	Page<Animal> findByOwnerIn(List<Person> persons, PageRequest pageable);

	List<Animal> findByOwnerAndAnimalType(Person person, AnimalType type);

	List<Animal> findByOwnerAndAnimalTypeId(Person person, Long typeId);

	long countByOwnerAndAnimalTypeId(Person person, Long typeId);
}
