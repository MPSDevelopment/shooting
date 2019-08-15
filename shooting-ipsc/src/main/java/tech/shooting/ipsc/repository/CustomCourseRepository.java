package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomCourseRepository {

	List<Course> findByPersonDivisionIn(Division division);
}
