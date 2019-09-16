package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.SuccessfulMessage;
import tech.shooting.ipsc.bean.CourseBean;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.service.CourseService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.COURSE_CONTROLLER)
@Api(value = ControllerAPI.COURSE_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class CourseController {

	@Autowired
	private CourseService courseService;

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_ALL_COURSES)
	@ApiOperation(value = "Return list courses if exist")
	public ResponseEntity<List<Course>> getAllCourses() {
		return new ResponseEntity<>(courseService.getAll(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_ID)
	@ApiOperation(value = "Return course if exist")
	public ResponseEntity<Course> getCourseById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_COURSE_ID) Long courseId) throws BadRequestException {
		return new ResponseEntity<>(courseService.getById(courseId), HttpStatus.OK);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_DELETE_COURSE_BY_ID)
	@ApiOperation(value = "Return status ok")
	public ResponseEntity<SuccessfulMessage> deleteCourseById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_COURSE_ID) Long courseId) {
		courseService.deleteCourse(courseId);
		return new ResponseEntity<>(new SuccessfulMessage("Course %s was deleted successfully", courseId), HttpStatus.OK);
	}

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_POST_COURSE)
	@ApiOperation(value = "Return  course")
	public ResponseEntity<Course> postCourse(@RequestBody @Valid CourseBean bean) throws BadRequestException {
		return new ResponseEntity<>(courseService.postCourse(bean), HttpStatus.CREATED);
	}

	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_PUT_COURSE)
	@ApiOperation(value = "Return updated course")
	public ResponseEntity<Course> putCourse(@PathVariable(value = ControllerAPI.PATH_VARIABLE_COURSE_ID) Long courseId, @RequestBody @Valid CourseBean bean) throws BadRequestException {
		return new ResponseEntity<>(courseService.putCourse(courseId, bean), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_DIVISION)
	@ApiOperation(value = "Return course list by division")
	public ResponseEntity<List<Course>> getCourseByDivision(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long divisionId) throws BadRequestException {
		return new ResponseEntity<>(courseService.getCourseByDivision(divisionId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURSE_BY_PERSON)
	@ApiOperation(value = "Return list courses by person")
	public ResponseEntity<List<Course>> getCourseByPerson(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long personId) throws BadRequestException {
		return new ResponseEntity<>(courseService.getCoursesByPerson(personId), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURCES_BY_DIVISION_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get cources for division by page ")
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", responseHeaders = { @ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class) }) })
	public ResponseEntity<List<Course>> getCoursesByDivision(@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long divisionId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return courseService.getCourcesByDivisionPage(divisionId, page, size);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COURCES_BY_PERSON_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get cources for person by page")
	@ApiResponses({ @ApiResponse(code = 200, message = "Success", responseHeaders = { @ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
			@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class) }) })
	public ResponseEntity<List<Course>> getCoursesByPerson(@PathVariable(value = ControllerAPI.PATH_VARIABLE_PERSON_ID) Long divisionId, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page,
			@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return courseService.getCourcesByPersonPage(divisionId, page, size);
	}
	
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.COURSE_CONTROLLER_GET_COUNT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get all course count")
	public ResponseEntity<Long> getCount() {
		return new ResponseEntity<>(courseService.getCount(), HttpStatus.OK);
	}
}
