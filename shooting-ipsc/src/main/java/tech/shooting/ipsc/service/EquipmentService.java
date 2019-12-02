package tech.shooting.ipsc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.EquipmentBean;
import tech.shooting.ipsc.pojo.EquipmentType;
import tech.shooting.ipsc.pojo.Equipment;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.EquipmentRepository;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;

@Service
public class EquipmentService {

	@Autowired
	private EquipmentRepository repository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private EquipmentTypeRepository typeRepository;

	public List<Equipment> getAll() {
		return repository.findAll();
	}

	public Equipment getById(Long id) throws BadRequestException {
		return checkVehicle(id);
	}

	private Equipment checkVehicle(Long id) throws BadRequestException {
		return repository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Equipment with this id %s is not exist", id)));
	}

	public Equipment save(EquipmentBean bean) throws BadRequestException {
		Person owner = checkPerson(bean.getOwner());
		Equipment equipment = bean.getId() == null ? null : repository.findById(bean.getId()).orElse(null);
		EquipmentType type = checkType(bean.getType());
		if (equipment == null) {
			equipment = new Equipment();
		}
		equipment.setOwner(owner).setSerialNumber(bean.getSerialNumber()).setType(type);

		return repository.save(equipment);
	}

	private EquipmentType checkType(Long type) throws BadRequestException {
		return typeRepository.findById(type).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect type id %s", type)));
	}

	private Division checkDivision(Long division) throws BadRequestException {
		return divisionRepository.findById(division).orElseThrow(() -> new BadRequestException(new ErrorMessage("Division with id %s is not exist", division)));
	}

	private Person checkPerson(Long owner) throws BadRequestException {
		if (owner==null) {
			return null;
		}
		return personRepository.findById(owner).orElse(null);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public Equipment addOwner(Long weaponId, Long personId) throws BadRequestException {
		Equipment vehicle = checkVehicle(weaponId);
		if (personId == null) {
			vehicle.setOwner(null);
		} else {
			vehicle.setOwner(checkPerson(personId));
		}
		return repository.save(vehicle);
	}

	public List<Equipment> getAllByDivision(Long divisionId) throws BadRequestException {
		return repository.findByPersonDivision(checkDivision(divisionId));
	}

	public List<Equipment> getAllByPerson(Long personId) throws BadRequestException {
		return repository.findByOwner(checkPerson(personId));
	}

	public List<Equipment> getAllByPerson(String personName, long divisionId) throws BadRequestException {
		return getAllByDivision(divisionId).stream().filter(weapon -> weapon.getOwner().getName().equalsIgnoreCase(personName)).collect(Collectors.toList());
	}
}
