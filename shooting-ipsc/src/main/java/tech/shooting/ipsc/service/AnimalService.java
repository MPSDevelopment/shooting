package tech.shooting.ipsc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.AnimalBean;
import tech.shooting.ipsc.pojo.Animal;
import tech.shooting.ipsc.pojo.AnimalType;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.AnimalRepository;
import tech.shooting.ipsc.repository.AnimalTypeRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;

@Service
public class AnimalService {

	@Autowired
	private AnimalRepository repository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private AnimalTypeRepository typeRepository;

	public List<Animal> getAll() {
		return repository.findAll();
	}

	public Animal getById(Long id) throws BadRequestException {
		return checkAnimal(id);
	}

	private Animal checkAnimal(Long id) throws BadRequestException {
		return repository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Animal with this id %s is not exist", id)));
	}

	public Animal save(AnimalBean bean) throws BadRequestException {
		Person owner = checkPerson(bean.getOwner());
		Animal animal = bean.getId() == null ? null : repository.findById(bean.getId()).orElse(null);
		AnimalType type = checkType(bean.getType());
		if (animal == null) {
			animal = new Animal();
		}
		animal.setOwner(owner).setType(type).setName(bean.getName());

		return repository.save(animal);
	}

	private AnimalType checkType(Long type) throws BadRequestException {
		return typeRepository.findById(type).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect type id %s", type)));
	}

	private Division checkDivision(Long division) throws BadRequestException {
		return divisionRepository.findById(division).orElseThrow(() -> new BadRequestException(new ErrorMessage("Division with id %s is not exist", division)));
	}

	private Person checkPerson(Long owner) throws BadRequestException {
		return personRepository.findById(owner).orElseThrow(() -> new BadRequestException(new ErrorMessage("Person with id %s is not exist", owner)));
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public Animal addOwner(Long animalId, Long personId) throws BadRequestException {
		Animal vehicle = checkAnimal(animalId);
		if (personId == null) {
			vehicle.setOwner(null);
		} else {
			vehicle.setOwner(checkPerson(personId));
		}
		return repository.save(vehicle);
	}

	public List<Animal> getAllByDivision(Long divisionId) throws BadRequestException {
		return repository.findByPersonDivision(checkDivision(divisionId));
	}

	public List<Animal> getAllByPerson(Long personId) throws BadRequestException {
		return repository.findByOwner(checkPerson(personId));
	}

	public List<Animal> getAllByPerson(String personName, long divisionId) throws BadRequestException {
		return getAllByDivision(divisionId).stream().filter(weapon -> weapon.getOwner().getName().equalsIgnoreCase(personName)).collect(Collectors.toList());
	}
}
