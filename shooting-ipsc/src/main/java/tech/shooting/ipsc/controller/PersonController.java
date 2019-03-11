package tech.shooting.ipsc.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
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

    @PostMapping(value = ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_POST_CREATE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    @ApiOperation(value = "Add new person", notes = "Create new person")
    public ResponseEntity<Person> createPerson (@RequestBody @Valid PersonBean personBean) throws BadRequestException {
        Person person = new Person();
        BeanUtils.copyProperties(personBean, person);
        createPerson(person);
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

    private void createPerson (Person person) {
        log.info("Create person with name %s and code ipsc %s", person.getName(), person.getRifleCodeIpsc());
        if(personRepository.findByNameAndRifleCodeIpsc(person.getName(), person.getRifleCodeIpsc()) != null) {
            throw new ValidationException(Person.NAME_AND_IPSC_FIELD, "Person with name %s and rifle ipsc code %s is already exist", person.getName(), person.getRifleCodeIpsc());
        }
        person.setActive(true);
        personRepository.save(person);

    }

    @GetMapping(value = ControllerAPI.PERSON_CONTROLLER + ControllerAPI.VERSION_1_0 + ControllerAPI.PERSON_CONTROLLER_GET_PERSON, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    @ApiOperation(value = "Get person by id", notes = "Return person object")
    public ResponseEntity<Person> getPerson(@PathVariable(value = "{personId}",required = true) Long personId) throws BadRequestException{
        Person person = personRepository.findById(personId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", personId)));
        return new ResponseEntity<>(person,HttpStatus.OK);
    }
}
