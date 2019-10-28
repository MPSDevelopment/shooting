package tech.shooting.ipsc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.VehicleBean;
import tech.shooting.ipsc.bean.WeaponBean;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Vehicle;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.VehicleRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private VehicleTypeRepository typeRepository;

	public List<Vehicle> getAll() {
		return vehicleRepository.findAll();
	}

	public Vehicle getVehicleById(Long id) throws BadRequestException {
		return checkVehicle(id);
	}

	private Vehicle checkVehicle(Long id) throws BadRequestException {
		return vehicleRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Vehicle with this id %s is not exist", id)));
	}

	public Vehicle post(VehicleBean bean) throws BadRequestException {
		Person owner = checkPerson(bean.getOwner());
		Vehicle vehicle = bean.getId() == null ? null : vehicleRepository.findById(bean.getId()).orElse(null);
		VehicleType type = checkType(bean.getType());
		if (vehicle == null) {
			vehicle = new Vehicle().setOwner(owner).setSerialNumber(bean.getSerialNumber()).setPassportNumber(bean.getPassportNumber()).setCount(bean.getCount()).setType(type);
		} else {
			vehicle.setOwner(owner).setSerialNumber(bean.getSerialNumber()).setPassportNumber(bean.getPassportNumber()).setCount(bean.getCount()).setType(type);
		}
		return vehicleRepository.save(vehicle);
	}

	private VehicleType checkType(Long weaponType) throws BadRequestException {
		return typeRepository.findById(weaponType).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect weapon type id %s", weaponType)));
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

	public Vehicle addOwner(Long weaponId, Long personId) throws BadRequestException {
		Vehicle vehicle = checkVehicle(weaponId);
		if (personId == null) {
			vehicle.setOwner(null);
		} else {
			vehicle.setOwner(checkPerson(personId));
		}
		return vehicleRepository.save(vehicle);
	}

	public Vehicle addNumberOfShootingForWeapon(Long weaponId, Integer firedCount) throws BadRequestException {
		Vehicle vehicle = checkVehicle(weaponId);
		checkNumberShootings(vehicle.getCount(), firedCount);
		return vehicleRepository.save(vehicle.setCount(firedCount));
	}

	private void checkNumberShootings(Integer count, Integer firedCount) throws BadRequestException {
		if (count > firedCount) {
			throw new BadRequestException(new ErrorMessage("You try set incorrect data count " + firedCount + " must be more than previous count "));
		}
	}

	public List<Vehicle> getAllByDivision(Long divisionId) throws BadRequestException {
		return vehicleRepository.findByPersonDivision(checkDivision(divisionId));
	}

	public List<Vehicle> getAllByPerson(Long personId) throws BadRequestException {
		return vehicleRepository.findByOwner(checkPerson(personId));
	}

	public List<Vehicle> getAllByPerson(String personName, long divisionId) throws BadRequestException {
		return getAllByDivision(divisionId).stream().filter(weapon -> weapon.getOwner().getName().equalsIgnoreCase(personName)).collect(Collectors.toList());
	}
}
