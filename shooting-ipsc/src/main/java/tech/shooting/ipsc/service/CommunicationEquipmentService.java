package tech.shooting.ipsc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.CommunicationEquipmentBean;
import tech.shooting.ipsc.bean.VehicleBean;
import tech.shooting.ipsc.bean.WeaponBean;
import tech.shooting.ipsc.pojo.CommunicationEquipmentType;
import tech.shooting.ipsc.pojo.CommunicationEquipment;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Vehicle;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.CommunicationEquipmentRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentTypeRepository;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.VehicleRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class CommunicationEquipmentService {

	@Autowired
	private CommunicationEquipmentRepository vehicleRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private CommunicationEquipmentTypeRepository typeRepository;

	public List<CommunicationEquipment> getAll() {
		return vehicleRepository.findAll();
	}

	public CommunicationEquipment getById(Long id) throws BadRequestException {
		return checkVehicle(id);
	}

	private CommunicationEquipment checkVehicle(Long id) throws BadRequestException {
		return vehicleRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Communication equipment with this id %s is not exist", id)));
	}

	public CommunicationEquipment post(CommunicationEquipmentBean bean) throws BadRequestException {
		Person owner = checkPerson(bean.getOwner());
		CommunicationEquipment equipment = bean.getId() == null ? null : vehicleRepository.findById(bean.getId()).orElse(null);
		CommunicationEquipmentType type = checkType(bean.getType());
		if (equipment == null) {
			equipment = new CommunicationEquipment().setOwner(owner).setSerialNumber(bean.getSerialNumber()).setType(type);
		} else {
			equipment.setOwner(owner).setSerialNumber(bean.getSerialNumber()).setType(type);
		}
		return vehicleRepository.save(equipment);
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
		vehicleRepository.deleteById(id);
	}

	public CommunicationEquipment addOwner(Long weaponId, Long personId) throws BadRequestException {
		CommunicationEquipment vehicle = checkVehicle(weaponId);
		if (personId == null) {
			vehicle.setOwner(null);
		} else {
			vehicle.setOwner(checkPerson(personId));
		}
		return vehicleRepository.save(vehicle);
	}

	public List<CommunicationEquipment> getAllByDivision(Long divisionId) throws BadRequestException {
		return vehicleRepository.findByPersonDivision(checkDivision(divisionId));
	}

	public List<CommunicationEquipment> getAllByPerson(Long personId) throws BadRequestException {
		return vehicleRepository.findByOwner(checkPerson(personId));
	}

	public List<CommunicationEquipment> getAllByPerson(String personName, long divisionId) throws BadRequestException {
		return getAllByDivision(divisionId).stream().filter(weapon -> weapon.getOwner().getName().equalsIgnoreCase(personName)).collect(Collectors.toList());
	}
}
