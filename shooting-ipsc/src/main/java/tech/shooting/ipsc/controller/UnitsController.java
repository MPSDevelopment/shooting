//package tech.shooting.ipsc.controller;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import tech.shooting.commons.exception.BadRequestException;
//import tech.shooting.ipsc.bean.UnitBean;
//import tech.shooting.ipsc.pojo.Units;
//import tech.shooting.ipsc.service.UnitsService;
//
//import javax.validation.Valid;
//import javax.validation.constraints.NotNull;
//import java.util.List;
//
//@Controller
//@RequestMapping(ControllerAPI.UNITS_CONTROLLER)
//@Api(value = ControllerAPI.UNITS_CONTROLLER)
//@Slf4j
//@PreAuthorize("hasRole('ADMIN')")
//public class UnitsController {
//    @Autowired
//    private UnitsService unitsService;
//
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
//    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.UNITS_CONTROLLER_GET_ALL_UNITS)
//    @ApiOperation(value = "Get list name")
//    public ResponseEntity<List<Units>> getUnits() {
//        return new ResponseEntity<>(unitsService.getUnits(), HttpStatus.OK);
//    }
//
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
//    @GetMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.UNITS_CONTROLLER_GET_UNIT)
//    @ApiOperation(value = "Get  unit by id")
//    public ResponseEntity<Units> getUnit(@PathVariable(value = ControllerAPI.PATH_VARIABLE_UNIT_ID) @NotNull Long unitId) throws BadRequestException {
//        return new ResponseEntity<>(unitsService.getUnit(unitId), HttpStatus.OK);
//    }
//
//    @DeleteMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.UNITS_CONTROLLER_DELETE_UNIT)
//    @ApiOperation(value = "Status ok")
//    public ResponseEntity<Void> deleteUnit(@PathVariable(value = ControllerAPI.PATH_VARIABLE_UNIT_ID) @NotNull Long unitId) {
//        unitsService.deleteUnit(unitId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @PostMapping (value = ControllerAPI.VERSION_1_0 + ControllerAPI.UNITS_CONTROLLER_POST_UNIT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation(value = "Get created unit")
//    public ResponseEntity<Units> postUnit(@RequestBody @Valid UnitBean bean){
//        return new ResponseEntity<>(unitsService.postUnit(bean),HttpStatus.CREATED);
//    }
//}
