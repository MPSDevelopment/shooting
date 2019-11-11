package tech.shooting.ipsc.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CourseBean;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.CourseRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseService {
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	public List<Course> getAll() {
		return courseRepository.findAll();
	}

	public Course getById(Long courseId) throws BadRequestException {
		return checkCourse(courseId);
	}

	private Course checkCourse(Long courseId) throws BadRequestException {
		return courseRepository.findById(courseId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect course id %s", courseId)));
	}

	public void deleteCourse(Long courseId) {
		courseRepository.deleteById(courseId);
	}

	public Course postCourse(CourseBean bean) throws BadRequestException {
		Person person = checkPerson(bean.getOwner());
		Course course = new Course();
		BeanUtils.copyProperties(bean, course, Course.COURSE_PERSON);
		course.setOwner(person);
		return courseRepository.save(course);
	}

	private Person checkPerson(Long person) throws BadRequestException {
		return personRepository.findById(person).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", person)));
	}

	private Division checkDivision(Long division) throws BadRequestException {
		return divisionRepository.findById(division).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division id %s", division)));
	}

	public Course putCourse(Long courseId, CourseBean bean) throws BadRequestException {
		Course course = checkCourse(courseId);
		Person person = checkPerson(bean.getOwner());
		if (!course.getOwner().equals(person)) {
			new BadRequestException(new ErrorMessage("Owner in the course must be same %s and %s", course.getOwner().getId(), person.getId()));
		}

		BeanUtils.copyProperties(bean, course, Course.COURSE_PERSON);
		course.setOwner(person);
		return courseRepository.save(course);
	}

	public List<Course> getCourseByDivision(Long divisionId) throws BadRequestException {
		Division division = checkDivision(divisionId);
		return courseRepository.findByOwnerDivisionIn(division);
	}

	public List<Course> getCoursesByPerson(Long personId) throws BadRequestException {
		return courseRepository.findByOwner(checkPerson(personId));
	}

	public Page<Course> getAllCoursesByPersonPaging(Long personId, Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, Person.ID_FIELD);
		if (personId != null) {
			Person person = personRepository.findById(personId).orElseThrow(() -> new ValidationException(Division.ID_FIELD, "Person with id %s does not exist", personId));
			return courseRepository.findByOwnerIn(Arrays.asList(person), pageable);
		}
		return courseRepository.findAll(pageable);
	}

	public ResponseEntity<List<Course>> getCourcesByPersonPage(Long divisionId, Integer page, Integer size) {
		page = Math.max(1, page);
		page--;
		size = Math.min(Math.max(10, size), 20);
		Page<Course> pageOfUsers = getAllCoursesByPersonPaging(divisionId, page, size);
		return new ResponseEntity<>(pageOfUsers.getContent(), Pageable.setHeaders(page, pageOfUsers.getTotalElements(), pageOfUsers.getTotalPages()), HttpStatus.OK);

	}

	public Page<Course> getAllCoursesByDivisionPaging(Long divisionId, Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, Person.ID_FIELD);
		if (divisionId != null) {
			Division division = divisionRepository.findById(divisionId).orElseThrow(() -> new ValidationException(Division.ID_FIELD, "Division with id %s does not exist", divisionId));
			return courseRepository.findByOwnerDivisionIn(division, pageable);
		}
		return courseRepository.findAll(pageable);
	}

	public ResponseEntity<List<Course>> getCourcesByDivisionPage(Long divisionId, Integer page, Integer size) {
		page = Math.max(1, page);
		page--;
		size = Math.min(Math.max(10, size), 20);
		Page<Course> pageOfUsers = getAllCoursesByDivisionPaging(divisionId, page, size);
		return new ResponseEntity<>(pageOfUsers.getContent(), Pageable.setHeaders(page, pageOfUsers.getTotalElements(), pageOfUsers.getTotalPages()), HttpStatus.OK);

	}

	public Long getCount() {
		return courseRepository.count();
	}
}
