package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, Long>, CustomCourseRepository {

	List<Course> findByPerson(Person person);

	List<Course> findByPersonIn(List<Person> persons);

	Page<Course> findByPersonIn(List<Person> persons, PageRequest pageable);

//	List<Course> findByPersonDivision(Division division);

//	@Query(value = "{ 'person' : {'$elemMatch': { 'division.id' : ?0 }}}") // { "qty" : { "$elemMatch" : { "num" : 100 , "color" : "green"}}}
//	@Query("{'person' :{'$ref' : 'person' , 'division.id' : ?0}}")
	List<Course> findByPersonDivisionId(Long division);
}
