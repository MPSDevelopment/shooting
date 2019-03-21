package tech.shooting.ipsc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.exception.ValidationException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.PersonBean;
import tech.shooting.ipsc.bean.UpdatePerson;
import tech.shooting.ipsc.controller.PageAble;
import tech.shooting.ipsc.enums.ClassificationBreaks;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.PersonRepository;

import java.util.List;

@Slf4j
@Service
public class PersonService {
	@Autowired
	private PersonRepository personRepository;

	public List<Person> getAllPerson () {
		return personRepository.findAll();
	}

	private void createPerson (Person person) {
		if(personRepository.findByNameAndBirthDate(person.getName(), person.getBirthDate()) != null) {
			throw new ValidationException(Person.NAME_AND_BIRTHDAY, "Person with name %s and date of birthday %s is already exist", person.getName(), person.getBirthDate());
		}
		person.setActive(true);
		person.setQualifierRank(ClassificationBreaks.D);
		personRepository.save(person);
	}

	public Person createPerson (PersonBean personBean) {
		Person person = new Person();
		BeanUtils.copyProperties(personBean, person);
		createPerson(person);
		return person;
	}

	public Person getPersonByIdIfExist (Long personId) throws BadRequestException {
		return personRepository.findById(personId).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect person id %s", personId)));
	}

	public Person updatePerson (Long personId, UpdatePerson personBean) throws BadRequestException {
		if(!personId.equals(personBean.getId())) {
			throw new BadRequestException(new ErrorMessage("Path personId %s does not match bean personId %s", personId, personBean.getId()));
		}
		Person dbPerson = getPersonByIdIfExist(personId);
		BeanUtils.copyProperties(personBean, dbPerson);
		dbPerson = personRepository.save(dbPerson);
		return dbPerson;
	}

	public void removePersonIfExist (Long personId) throws BadRequestException {
		Person person = getPersonByIdIfExist(personId);
		personRepository.delete(person);
	}

	public ResponseEntity<List<Person>> getPersonByPage (Integer page, Integer size) {
		return PageAble.getPage(page, size, personRepository);
	}

	public Long getCount () {
		return personRepository.count();
	}
}