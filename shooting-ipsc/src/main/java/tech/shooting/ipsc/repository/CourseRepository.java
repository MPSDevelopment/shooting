package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, Long> {

	List<Course> findAllByDivision(Long division);

	List<Course> findAllByPerson(Person person);

	List<Course> findByDivisionIn(List<Long> list);

	Page<Course> findByDivisionIn(List<Long> list, PageRequest pageable);
}
