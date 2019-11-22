package tech.shooting.ipsc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CommunicationEquipmentBean;
import tech.shooting.ipsc.pojo.CommunicationEquipmentType;
import tech.shooting.ipsc.pojo.CommunicationEquipment;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.repository.CommunicationEquipmentRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentTypeRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;

@Service
public class CommunicationEquipmentService {

	@Autowired
	private CommunicationEquipmentRepository equipmentRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private CommunicationEquipmentTypeRepository typeRepository;

	public List<CommunicationEquipment> getAll() {
		return equipmentRepository.findAll();
	}

	public CommunicationEquipment getById(Long id) throws BadRequestException {
		return checkEquipment(id);
	}

	private CommunicationEquipment checkEquipment(Long id) throws BadRequestException {
		return equipmentRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Communication equipment with this id %s is not exist", id)));
	}

	public CommunicationEquipment save(CommunicationEquipmentBean bean) throws BadRequestException {
		Person owner = checkPerson(bean.getOwner());
		CommunicationEquipment equipment = bean.getId() == null ? null : equipmentRepository.findById(bean.getId()).orElse(null);
		CommunicationEquipmentType type = checkType(bean.getType());
		if (equipment == null) {
			equipment = new CommunicationEquipment();
		}
		equipment.setOwner(owner).setSerialNumber(bean.getSerialNumber()).setType(type);

		return equipmentRepository.save(equipment);
	}

	private CommunicationEquipmentType checkType(Long type) throws BadRequestException {
		return typeRepository.findById(type).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect type id %s", type)));
	}

	private Division checkDivision(Long division) throws BadRequestException {
		return divisionRepository.findById(division).orElseThrow(() -> new BadRequestException(new ErrorMessage("Division with id %s is not exist", division)));
	}

	private Person checkPerson(Long owner) throws BadRequestException {
		return personRepository.findById(owner).orElseThrow(() -> new BadRequestException(new ErrorMessage("Person with id %s is not exist", owner)));
	}

	public void delete(long id) {
		equipmentRepository.deleteById(id);
	}

	public CommunicationEquipment addOwner(Long weaponId, Long personId) throws BadRequestException {
		CommunicationEquipment vehicle = checkEquipment(weaponId);
		if (personId == null) {
			vehicle.setOwner(null);
		} else {
			vehicle.setOwner(checkPerson(personId));
		}
		return equipmentRepository.save(vehicle);
	}

	public List<CommunicationEquipment> getAllByDivision(Long divisionId) throws BadRequestException {
		return equipmentRepository.findByPersonDivision(checkDivision(divisionId));
	}

	public List<CommunicationEquipment> getAllByPerson(Long personId) throws BadRequestException {
		return equipmentRepository.findByOwner(checkPerson(personId));
	}

	public List<CommunicationEquipment> getAllByPerson(String personName, long divisionId) throws BadRequestException {
		return getAllByDivision(divisionId).stream().filter(weapon -> weapon.getOwner().getName().equalsIgnoreCase(personName)).collect(Collectors.toList());
	}
}
