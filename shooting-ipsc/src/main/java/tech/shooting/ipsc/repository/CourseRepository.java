package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Course;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course,Long> {
    List<Course> findAllByDivision(Long division);
}
