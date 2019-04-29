package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.pojo.Categories;
import tech.shooting.ipsc.pojo.Standard;
import tech.shooting.ipsc.pojo.Units;
import tech.shooting.ipsc.service.StandardService;

import java.util.List;

@Controller
@RequestMapping(value = ControllerAPI.STANDARD_CONTROLLER)
@Api(value = ControllerAPI.STANDARD_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<List<Standard>> getStandardsBySubject(@PathVariable(value = ControllerAPI.PATH_VARIABLE_SUBJECT) Long subjectId) throws BadRequestException {
        return new ResponseEntity<>(standardService.getStandardsBySubject(subjectId), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.STANDARD_CONTROLLER_GET_STANDARD_BY_ID)
    @ApiOperation(value = "Get standard by id")
    public ResponseEntity<Standard> getStandardById(@PathVariable(value = ControllerAPI.PATH_VARIABLE_STANDARD_ID) Long standardId) throws BadRequestException {
        return new ResponseEntity<>(standardService.getStandardById(standardId), HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0+ControllerAPI.STANDARD_CONTROLLER_GET_CATEGORIES)
    @ApiOperation(value = "Get list categories")
    public ResponseEntity<List<Categories>> getCategories(){
        return new ResponseEntity<>(standardService.getCategories(),HttpStatus.OK);
    }

    @GetMapping(value = ControllerAPI.VERSION_1_0+ControllerAPI.STANDARD_CONTROLLER_GET_UNITS)
    @ApiOperation(value = "Get list units")
    public ResponseEntity<List<Units>> getUnits(){
        return new ResponseEntity<>(standardService.getUnits(),HttpStatus.OK);
    }

}
