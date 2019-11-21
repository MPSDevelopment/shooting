package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.PersonBean;
import tech.shooting.ipsc.bean.ChangeRfidCodeBean;
import tech.shooting.ipsc.bean.NumberBean;
import tech.shooting.ipsc.bean.UpdatePerson;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.TypePresent;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.RankRepository;

import java.util.List;

import javax.validation.Valid;

@Slf4j
@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private RankRepository rankRepository;

	public List<Person> getAllPerson() {
		return personRepository.findAll();
	}

	public List<Person> getAllPersonsByDivision(Long divisionId) {
		Division division = divisionRepository.findById(divisionId).orElseThrow(() -> new ValidationException(Division.ID_FIELD, "Division with id %s does not exist", divisionId));
		List<Division> divisions = division.getAllChildren();
		return personRepository.findByDivisionIn(divisions);
	}

	public Page<Person> getAllPersonsByDivisionPaging(Long divisionId, Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, Person.ID_FIELD);
		if (divisionId != null) {
			Division division = divisionRepository.findById(divisionId).orElseThrow(() -> new ValidationException(Division.ID_FIELD, "Division with id %s does not exist", divisionId));
			List<Division> divisions = division.getAllChildren();
			return personRepository.findByDivisionIn(divisions, pageable);
		}
		return personRepository.findAll(pageable);
	}

	public Page<Person> getPersonListByDivisionPaging(Long divisionId, Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, Person.ID_FIELD);
		if (divisionId != null) {
			Division division = divisionRepository.findById(divisionId).orElseThrow(() -> new ValidationException(Division.ID_FIELD, "Division with id %s does not exist", divisionId));
			List<Division> divisions = division.getAllChildren();
			return personRepository.getPersonListByPage(divisions, pageable);
		}
		return personRepository.findAll(pageable);
	}

	private void createPerson(Person person) {
		if (personRepository.findByNameAndBirthDate(person.getName(), person.getBirthDate()) != null) {
			throw new ValidationException(Person.NAME_AND_BIRTHDAY, "Person with name %s and date of birthday %s is already exist", person.getName(), person.getBirthDate());
		}
		person.setActive(true);
		person.setQualifierRank(ClassificationBreaks.D);
		personRepository.save(person);
	}

	public Person createPerson(PersonBean personBean) throws BadRequestException {
		Person person = new Person();
		BeanUtils.copyProperties(personBean, person, Person.DIVISION, Person.RANK);
		if (personBean.getDivision() != null) {
			person.setDivision(divisionRepository.findById(personBean.getDivision()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division id %s", personBean.getDivision()))));
		}
		if (personBean.getRank() != null) {
			person.setRank(rankRepository.findById(personBean.getRank()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect rank id %s", personBean.getRank()))));
		}
		createPerson(person);
		return person;
	}

	public Person getPersonByIdIfExist(Long personId) throws BadRequestException {
		return personRepository.findById(personId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", personId)));
	}

	public Person getPersonByRfidCode(String code) throws BadRequestException {
		return personRepository.findByRfidCode(code).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person rfid code %s", code)));
	}

	public Person getPersonByNumber(String number) throws BadRequestException {
		return personRepository.findByNumber(number).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person number %s", number)));
	}

	public Person getPersonByCall(String call) throws BadRequestException {
		return personRepository.findByCall(call).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person call %s", call)));
	}

	public Person updatePerson(Long personId, UpdatePerson personBean) throws BadRequestException {
		if (!personId.equals(personBean.getId())) {
			throw new BadRequestException(new ErrorMessage("Path personId %s does not match bean personId %s", personId, personBean.getId()));
		}
		Person dbPerson = getPersonByIdIfExist(personId);
		BeanUtils.copyProperties(personBean, dbPerson, Person.DIVISION, Person.RANK);
		if (personBean.getDivision() != null) {
			dbPerson.setDivision(divisionRepository.findById(personBean.getDivision()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect division id %s", personBean.getDivision()))));
		}
		if (personBean.getRank() != null) {
			dbPerson.setRank(rankRepository.findById(personBean.getRank()).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect rank id %s", personBean.getRank()))));
		}

		dbPerson = personRepository.save(dbPerson);
		return dbPerson;
	}

	public Person updatePerson(@Valid ChangeRfidCodeBean personBean) throws BadRequestException {
		Person dbPerson = getPersonByIdIfExist(personBean.getId());
		dbPerson.setRfidCode(personBean.getRfidCode());
		return personRepository.save(dbPerson);
	}

	public void removePersonIfExist(Long personId) throws BadRequestException {
		Person person = getPersonByIdIfExist(personId);
		personRepository.deleteById(person.getId());
	}

	public ResponseEntity<List<Person>> getPersonByPage(Long rootId, Integer page, Integer size) {
		page = Math.max(1, page);
		page--;
		size = Math.min(Math.max(10, size), 20);
		Page<Person> pageOfUsers = getPersonListByDivisionPaging(rootId, page, size);
		return new ResponseEntity<>(pageOfUsers.getContent(), Pageable.setHeaders(page, pageOfUsers.getTotalElements(), pageOfUsers.getTotalPages()), HttpStatus.OK);
	}

	public Long getCount() {
		return personRepository.count();
	}

	public List<TypePresent> getTypePresent() {
		return TypeOfPresence.getList();
	}

	public NumberBean getFreeRfid() {
		Integer rfidCode = 1000;
		while (personRepository.findByRfidCode(String.valueOf(rfidCode)).orElse(null) != null) {
			rfidCode++;
		}
		return new NumberBean(String.valueOf(rfidCode));
	}

	public NumberBean getFreeNumber() {
		Integer number = 1;
		while (personRepository.findByNumber(String.valueOf(number)).orElse(null) != null) {
			number++;
		}
		return new NumberBean(String.valueOf(number));
	}
}
