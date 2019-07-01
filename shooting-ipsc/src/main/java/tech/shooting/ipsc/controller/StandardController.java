package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.StandardBean;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.service.StandardService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.STANDARD_CONTROLLER)
@Api(value = ControllerAPI.STANDARD_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class StandardController {
    @Autowired
    private StandardService standardService;

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_ALL)
    @ApiOperation(value = "Get list all standard")
    public ResponseEntity<List<Standard>> getAllStandards() {
        return new ResponseEntity<>(standardService.getAllStandards(), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_SUBJECT)
    @ApiOperation(value = "Get list standards by subject")
    public ResponseEntity<List<Standard>> getStandardsBySubject(@PathVariable(value = ControllerAPI.PATH_VARIABLE_SUBJECT_ID) Long subjectId) throws BadRequestException {
        return new ResponseEntity<>(standardService.getStandardsBySubject(subjectId), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID)
    @ApiOperation(value = "Get standard by id")
    public ResponseEntity<Standard> getStandardById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) throws BadRequestException {
        return new ResponseEntity<>(standardService.getStandardById(standardId), HttpStatus.OK);
    }

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_POST_STANDARD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Get created standard")
    public ResponseEntity<Standard> postStandard(@RequestBody @Valid StandardBean bean) throws BadRequestException{
        return new ResponseEntity<>(standardService.postStandard(bean),HttpStatus.CREATED);
    }

    @PutMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_PUT_STANDARD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Get updated standard")
    public ResponseEntity<Standard> putStandard(@PathVariable (value = ControllerAPI.PATH_VARIABLE_STANDARD_ID)Long standardId,@RequestBody @Valid StandardBean bean) throws BadRequestException{
        return new ResponseEntity<>(standardService.putStandard(standardId,bean),HttpStatus.OK);
    }

    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_DELETE_STANDARD_BY_ID)
    @ApiOperation(value = "Get status ok if all ok")
    public ResponseEntity deleteStandardById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) {
        standardService.deleteStandardById(standardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
