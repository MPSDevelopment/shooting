package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.OperationBean;
import tech.shooting.ipsc.bean.OperationCombatListHeaderBean;
import tech.shooting.ipsc.bean.StandardBean;
import tech.shooting.ipsc.bean.StandardCommonConditionsBean;
import tech.shooting.ipsc.enums.UnitEnum;
import tech.shooting.ipsc.pojo.Course;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.StandardCommonConditions;
import tech.shooting.ipsc.repository.AmmoTypeRepository;
import tech.shooting.ipsc.repository.AnimalTypeRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentTypeRepository;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;
import tech.shooting.ipsc.repository.OperationRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class OperationService {

	private static final String WEAPON_TYPE_HEADER = "weaponry";

	private static final String AMMO_TYPE_HEADER = "ammo";

	private static final String ANIMAL_TYPE_HEADER = "animal";

	private static final String VEHICLE_TYPE_HEADER = "vehicle";

	private static final String COMMUNICATION_TYPE_HEADER = "communication";

	private static final String EQUIPMENT_TYPE_HEADER = "equipment";

	@Autowired
	private OperationRepository operationRepository;

	@Autowired
	private WeaponTypeRepository weaponTypeRepository;

	@Autowired
	private AmmoTypeRepository ammoTypeRepository;

	@Autowired
	private AnimalTypeRepository animalTypeRepository;

	@Autowired
	private VehicleTypeRepository vehicleTypeRepository;

	@Autowired
	private CommunicationEquipmentTypeRepository communicationEquipmentTypeRepository;

	@Autowired
	private EquipmentTypeRepository equipmentTypeRepository;

	public List<OperationCombatListHeaderBean> getHeaders() {

		var result = new ArrayList<OperationCombatListHeaderBean>();

		weaponTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(WEAPON_TYPE_HEADER));
		});
		ammoTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(AMMO_TYPE_HEADER));
		});
		animalTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(ANIMAL_TYPE_HEADER));
		});
		vehicleTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(VEHICLE_TYPE_HEADER));
		});
		communicationEquipmentTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(COMMUNICATION_TYPE_HEADER).setSubtype(type.getType().toString()));
		});
		equipmentTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(EQUIPMENT_TYPE_HEADER).setSubtype(type.getType().toString()));
		});

		return result;
	}

	public List<Operation> getAllOperations() {
		return operationRepository.findAll();
	}

	public Operation getOperationById(Long id) throws BadRequestException {
		return checkOperation(id);
	}
	
	private Operation checkOperation(Long id) throws BadRequestException {
		return operationRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect operation id %s", id)));
	}
	
	public Operation postOperation(OperationBean bean) throws BadRequestException {
		var operation = new Operation();
		BeanUtils.copyProperties(bean, operation);
		return operationRepository.save(operation);
	}

	public Operation putOperation(Long id, OperationBean bean) throws BadRequestException {
		var operation = checkOperation(id);
		BeanUtils.copyProperties(bean, operation);
		return operationRepository.save(operation);
	}

	public void deleteOperationById(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		operationRepository.deleteById(id);
	}

}
