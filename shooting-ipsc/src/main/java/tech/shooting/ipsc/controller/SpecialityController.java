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
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.ipsc.pojo.Speciality;
import tech.shooting.ipsc.service.SpecialityService;

import java.util.List;

@Controller
@RequestMapping(ControllerAPI.SPECIALITY_CONTROLLER)
@Api(value = ControllerAPI.SPECIALITY_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SpecialityController {

    @Autowired
    private SpecialityService specialityService;

   @GetMapping(value = ControllerAPI.VERSION_1_0+ControllerAPI.SPECIALITY_CONTROLLER_GET_ALL_SPECIALITY)
    @ApiOperation(value = "Get  list speciality")
    public ResponseEntity<List<Speciality>> getAllSpeciality ()  {
        return new ResponseEntity<>(specialityService.getAll(), HttpStatus.OK);
    }
}
