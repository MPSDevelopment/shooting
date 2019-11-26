package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.OperationBean;
import tech.shooting.ipsc.bean.OperationCombatListHeaderBean;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.OperationSignal;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Weather;
import tech.shooting.ipsc.repository.AmmoTypeRepository;
import tech.shooting.ipsc.repository.AnimalRepository;
import tech.shooting.ipsc.repository.AnimalTypeRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentTypeRepository;
import tech.shooting.ipsc.repository.EquipmentRepository;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;
import tech.shooting.ipsc.repository.OperationRepository;
import tech.shooting.ipsc.repository.VehicleRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
public class OperationService {

	private static final String REPLACEMENT = "0";

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

	@Autowired
	private WeaponRepository weaponRepository;

	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private CommunicationEquipmentRepository communicationEquipmentRepository;

	@Autowired
	private EquipmentRepository equipmentRepository;

	public void clearTypes() {
		weaponTypeRepository.deleteAll();
		ammoTypeRepository.deleteAll();
		animalTypeRepository.deleteAll();
		vehicleTypeRepository.deleteAll();
		communicationEquipmentTypeRepository.deleteAll();
		equipmentTypeRepository.deleteAll();
	}

	public void clearObjects() {
		weaponRepository.deleteAll();
		animalRepository.deleteAll();
		vehicleRepository.deleteAll();
		communicationEquipmentRepository.deleteAll();
		equipmentRepository.deleteAll();
	}

	public List<OperationCombatListHeaderBean> getHeaders() {

		var result = new ArrayList<OperationCombatListHeaderBean>();

		weaponTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(WEAPON_TYPE_HEADER).setTypeId(type.getId()));
		});
		ammoTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(AMMO_TYPE_HEADER).setTypeId(type.getId()));
		});
		animalTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(ANIMAL_TYPE_HEADER).setTypeId(type.getId()));
		});
		vehicleTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(VEHICLE_TYPE_HEADER).setTypeId(type.getId()));
		});
		communicationEquipmentTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(COMMUNICATION_TYPE_HEADER).setSubtype(type.getType().toString()).setTypeId(type.getId()));
		});
		equipmentTypeRepository.findAll().forEach(type -> {
			result.add(new OperationCombatListHeaderBean().setName(type.getName()).setType(EQUIPMENT_TYPE_HEADER).setSubtype(type.getType().toString()).setTypeId(type.getId()));
		});

		return result;
	}

	public List<List<String>> getCombatListData(Long id, List<OperationCombatListHeaderBean> headers) throws BadRequestException {
		var list = new ArrayList<List<String>>();
		var operation = checkOperation(id);

		var persons = operation.getParticipants().stream().map(item -> {
			return item.getPerson();
		}).collect(Collectors.toList());

		operation.getParticipants().forEach(participant -> {
			var participantData = new ArrayList<String>();

			headers.forEach(header -> {
				switch (header.getType()) {
				case WEAPON_TYPE_HEADER: {
					participantData.add(String.valueOf(weaponRepository.countByOwnerAndWeaponTypeId(participant.getPerson(), header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case AMMO_TYPE_HEADER: {
					
					break;
				}
				case ANIMAL_TYPE_HEADER: {
					participantData.add(String.valueOf(animalRepository.countByOwnerAndTypeId(participant.getPerson(), header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case VEHICLE_TYPE_HEADER: {
					participantData.add(String.valueOf(vehicleRepository.countByOwnerAndTypeId(participant.getPerson(), header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case COMMUNICATION_TYPE_HEADER: {
					participantData.add(String.valueOf(communicationEquipmentRepository.countByOwnerAndTypeId(participant.getPerson(), header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case EQUIPMENT_TYPE_HEADER: {
					participantData.add(String.valueOf(equipmentRepository.countByOwnerAndTypeId(participant.getPerson(), header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				}
			});

			if (CollectionUtils.isNotEmpty(participantData)) {
				list.add(participantData);
			}
		});
		return list;
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
		checkOperation(id);
		operationRepository.deleteById(id);
	}

	public void setWeather(Long id, Weather weather) throws BadRequestException {
		operationRepository.setWeatherToOperation(id, weather);
	}

	public void setSymbols(Long id, List<OperationSymbol> symbols) throws BadRequestException {
		operationRepository.setSymbolsToOperation(id, symbols);
	}
	
	public void setMainIndicatorsToOperation(Long id, List<OperationMainIndicator> indicators) throws BadRequestException {
		operationRepository.setMainIndicatorsToOperation(id, indicators);
	}
	
	public void setParticipantsToOperation(Long id, List<OperationParticipant> participants) throws BadRequestException {
		operationRepository.setParticipantsToOperation(id, participants);
	}
	
	public void setCombatSignals(Long id, List<OperationSignal> signals) throws BadRequestException {
		operationRepository.setCombatSignalsToOperation(id, signals);
	}

}
