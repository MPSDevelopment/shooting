package tech.shooting.ipsc.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.ipsc.bean.PersonBean;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.PersonRepository;

import javax.validation.Valid;

@Controller
@RequestMapping(ControllerAPI.PERSON_CONTROLLER)
@Api(value = ControllerAPI.PERSON_CONTROLLER)
@Slf4j
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE , produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    @ApiOperation(value = "Add new person", notes = "Create new person")
    public ResponseEntity <Person> createPerson (HttpRequest httpRequest, @RequestBody @Valid PersonBean personBean) throws BadRequestException {

    }
}
