package tech.shooting.ipsc.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.DivisionBean;
import tech.shooting.ipsc.bean.DivisionDropList;
import tech.shooting.ipsc.bean.UpdateDivisionBean;
import tech.shooting.ipsc.service.DivisionService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ControllerAPI.DIVISION_CONTROLLER)
@Api(value = ControllerAPI.DIVISION_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class DivisionController {
	@Autowired
	private DivisionService divisionService;

	@PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_POST_DIVISION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Add new division", notes = "Return created division")
	public ResponseEntity<DivisionBean> createDivision (@RequestBody @Valid DivisionBean divisionBean) {
		return new ResponseEntity<>(divisionService.createDivision(divisionBean, divisionBean.getParent()), HttpStatus.CREATED);
	}

	@DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_DELETE_DIVISION)
	@ApiOperation(value = "Remove division by id", notes = "Return status ok if removed successfully")
	public ResponseEntity removeDivision (@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long id) throws BadRequestException {
		divisionService.removeDivision(id);
		return new ResponseEntity(HttpStatus.CREATED);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_ALL)
	@ApiOperation(value = "Get all division", notes = "Return list divisions")
	public ResponseEntity<List<DivisionDropList>> getAllDivision () {
		return new ResponseEntity<>(divisionService.findAllDivisions(), HttpStatus.OK);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_PAGE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Get division by page")
	@ApiResponses({@ApiResponse(code = 200, message = "Success", responseHeaders = {@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGE, description = "Current page number", response = String.class),
		@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_TOTAL, description = "Total records in database", response = String.class),
		@ResponseHeader(name = ControllerAPI.HEADER_VARIABLE_PAGES, description = "Total pages in database", response = String.class)})})
	public ResponseEntity getDivisionByPage (@PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_NUMBER) Integer page, @PathVariable(value = ControllerAPI.PATH_VARIABLE_PAGE_SIZE) Integer size) {
		return divisionService.getDivisionByPage(page, size);
	}

	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_BY_ID)
	@ApiOperation(value = "Get division by id", notes = "Return division object")
	public ResponseEntity<DivisionBean> getDivisionById (@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long id) throws BadRequestException {
		return new ResponseEntity<>(divisionService.getDivision(id), HttpStatus.OK);
	}

//	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_PUT_DIVISION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	@ApiOperation(value = "Update division", notes = "Return update division object")
//	public ResponseEntity<DivisionBean> updateDivision (@RequestBody @Valid UpdateDivisionBean updateDivisionBean) {
//		return new ResponseEntity<>(divisionService.updateDivision(updateDivisionBean.getId(), updateDivisionBean.getName()), HttpStatus.OK);
//	}
	
	@PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_PUT_DIVISION, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Update division", notes = "Return update division object")
	public ResponseEntity<DivisionBean> updateDivision (@PathVariable(value = ControllerAPI.PATH_VARIABLE_DIVISION_ID) Long id, @RequestBody @Valid UpdateDivisionBean updateDivisionBean) throws BadRequestException {
		if (id==null || !id.equals(updateDivisionBean.getId())) {
			throw new BadRequestException(new ErrorMessage("Incorrect division %s", id));	
		}
		return new ResponseEntity<>(divisionService.updateDivision(updateDivisionBean.getId(), updateDivisionBean.getName()), HttpStatus.OK);
	}


	@PreAuthorize("hasRole('ADMIN') or  hasRole('USER')")
	@GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.DIVISION_CONTROLLER_GET_DIVISION_ROOT)
	@ApiOperation(value = "Return root division", notes = "Return root division object")
	public ResponseEntity<DivisionBean> getRoot () {
		return new ResponseEntity<>(divisionService.getRoot(), HttpStatus.OK);
	}
}
