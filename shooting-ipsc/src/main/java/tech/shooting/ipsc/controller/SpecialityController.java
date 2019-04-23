package tech.shooting.ipsc.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.ipsc.service.SpecialityService;

@Controller
@RequestMapping(ControllerAPI.SPECIALITY_CONTROLLER)
@Api(value = ControllerAPI.SPECIALITY_CONTROLLER)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SpecialityController {
    @Autowired
    private SpecialityService specialityService;


}
