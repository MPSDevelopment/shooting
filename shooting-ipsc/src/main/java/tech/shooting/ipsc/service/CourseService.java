package tech.shooting.ipsc.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CourseBean;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.CourseRepository;
import tech.shooting.ipsc.repository.PersonRepository;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PersonRepository personRepository;

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Course getById(Long courseId) throws BadRequestException {
        return checkCourse(courseId);
    }

    private Course checkCourse(Long courseId) throws BadRequestException {
        return courseRepository.findById(courseId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect course id %s",courseId)));
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    public Course postCourse(CourseBean bean) throws BadRequestException {
        Person person = checkPerson(bean.getPerson());
        Course course = new Course();
        BeanUtils.copyProperties(bean,course,Course.COURSE_PERSON);
        course.setPerson(person);
        course.setDivision(person.getDivision().getId());
        return courseRepository.save(course);
    }

    private Person checkPerson(Long person) throws BadRequestException {
        return personRepository.findById(person).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect person id %s",person)));
    }

    public Course putCourse(Long courseId, CourseBean bean) throws BadRequestException {
        Course course = checkCourse(courseId);
        Person person = checkPerson(bean.getPerson());
        if (!course.getPerson().equals(person)) {
            new BadRequestException(new ErrorMessage("Person in the course must be same %s and %s", course.getPerson().getId(), person.getId()));
        }

        BeanUtils.copyProperties(bean, course, Course.COURSE_PERSON);
        course.setPerson(person);
        return courseRepository.save(course);
    }

    public List<Course> getCourseByDivision(Long divisionId) {
        List<Course> allByDivision = courseRepository.findAllByDivision(divisionId);
        return allByDivision;
    }
}
