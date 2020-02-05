package tech.shooting.ipsc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.OperationBean;
import tech.shooting.ipsc.bean.OperationCombatElementBean;
import tech.shooting.ipsc.bean.OperationCombatListHeaderBean;
import tech.shooting.ipsc.bean.OperationCommandantServiceBean;
import tech.shooting.ipsc.controller.Pageable;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationCombatElement;
import tech.shooting.ipsc.pojo.OperationCommandantService;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationRoute;
import tech.shooting.ipsc.pojo.OperationSignal;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weather;
import tech.shooting.ipsc.repository.AmmoTypeRepository;
import tech.shooting.ipsc.repository.AnimalRepository;
import tech.shooting.ipsc.repository.AnimalTypeRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentRepository;
import tech.shooting.ipsc.repository.CommunicationEquipmentTypeRepository;
import tech.shooting.ipsc.repository.EquipmentRepository;
import tech.shooting.ipsc.repository.EquipmentTypeRepository;
import tech.shooting.ipsc.repository.OperationRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.VehicleRepository;
import tech.shooting.ipsc.repository.VehicleTypeRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

@Service
@Slf4j
public class OperationService {

	private static final String REPLACEMENT = "0";

	private static final String CALL_TYPE_HEADER = "call";

	private static final String WEAPON_TYPE_HEADER = "weaponry";

	private static final String AMMO_TYPE_HEADER = "ammo";

	private static final String ANIMAL_TYPE_HEADER = "animal";

	private static final String VEHICLE_TYPE_HEADER = "vehicle";

	private static final String COMMUNICATION_TYPE_HEADER = "communication";

	private static final String EQUIPMENT_TYPE_HEADER = "equipment";

	@Autowired
	private PersonRepository personRepository;

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

	public ResponseEntity<List<Operation>> getOperationsByPage(Integer page, Integer size) {
		page = Math.max(1, page);
		page--;
		size = Math.min(Math.max(10, size), 20);
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.ASC, Person.ID_FIELD);
		Page<Operation> pageOfUsers = operationRepository.findAll(pageable);
		return new ResponseEntity<>(pageOfUsers.getContent(), Pageable.setHeaders(page, pageOfUsers.getTotalElements(), pageOfUsers.getTotalPages()), HttpStatus.OK);
	}

	public List<OperationCombatListHeaderBean> getHeaders() {

		var result = new ArrayList<OperationCombatListHeaderBean>();

		result.add(new OperationCombatListHeaderBean().setType(CALL_TYPE_HEADER));

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

		var persons = operation.getParticipants();

		operation.getParticipants().forEach(participant -> {
			var participantData = new ArrayList<String>();

			headers.forEach(header -> {
				switch (header.getType()) {
				case CALL_TYPE_HEADER: {
					participantData.add(participant.getCall());
					break;
				}
				case WEAPON_TYPE_HEADER: {
					participantData.add(String.valueOf(weaponRepository.countByOwnerAndWeaponTypeId(participant, header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case AMMO_TYPE_HEADER: {

					break;
				}
				case ANIMAL_TYPE_HEADER: {
					participantData.add(String.valueOf(animalRepository.countByOwnerAndTypeId(participant, header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case VEHICLE_TYPE_HEADER: {
					participantData.add(String.valueOf(vehicleRepository.countByOwnerAndTypeId(participant, header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case COMMUNICATION_TYPE_HEADER: {
					participantData.add(String.valueOf(communicationEquipmentRepository.countByOwnerAndTypeId(participant, header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				case EQUIPMENT_TYPE_HEADER: {
					participantData.add(String.valueOf(equipmentRepository.countByOwnerAndTypeId(participant, header.getTypeId())).replace("0", REPLACEMENT));
					break;
				}
				default: {
					log.error("No combat list data for type %s", header.getType());
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

	public Weather getWeather(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getWeather();
	}

	public void setSymbols(Long id, List<OperationSymbol> symbols) throws BadRequestException {
		operationRepository.setSymbolsToOperation(id, symbols);
	}

	public List<OperationSymbol> getSymbols(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getSymbols();
	}

	public void setMainIndicatorsToOperation(Long id, List<OperationMainIndicator> indicators) throws BadRequestException {
		operationRepository.setMainIndicatorsToOperation(id, indicators);
	}

	public List<OperationMainIndicator> getMainIndicators(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getMainIndicators();
	}

	public List<Person> setParticipantsToOperation(Long operationId, List<Long> list) throws BadRequestException {

		var participants = new ArrayList<Person>();

		for (long id : list) {
			participants.add(checkPerson(id));
		}

		operationRepository.setParticipantsToOperation(operationId, participants);

		return participants;
	}

	public List<Person> getParticipants(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getParticipants();
	}

	public void setCombatSignals(Long id, List<OperationSignal> signals) throws BadRequestException {
		operationRepository.setCombatSignalsToOperation(id, signals);
	}

	public List<OperationSignal> getSignals(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getSignals();
	}

	public void setCommandantServices(Long id, List<OperationCommandantServiceBean> beans) throws BadRequestException {

		var services = new ArrayList<OperationCommandantService>();
		for (var bean : beans) {
			var service = new OperationCommandantService();
			BeanUtils.copyProperties(bean, service, OperationCommandantService.COMMANDANT);
			service.setCommandant(checkPerson(bean.getCommandant()));
		}

		operationRepository.setCommandantServicesToOperation(id, services);
	}

	public List<OperationCommandantService> getCommandantServices(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getCommandantServices();
	}

	public void setCombatElements(Long id, List<OperationCombatElementBean> beans) throws BadRequestException {

		var elements = new ArrayList<OperationCombatElement>();
		for (var bean : beans) {
			var element = new OperationCombatElement();
			BeanUtils.copyProperties(bean, element, OperationCombatElement.COMMANDER, OperationCombatElement.PARTICIPANTS);
			element.setCommander(checkPerson(bean.getCommander()));

			for (var participantBean : bean.getParticipants()) {
				element.getParticipants().add(checkPerson(participantBean));
			}

			elements.add(element);
		}

		operationRepository.setCombatElementsToOperation(id, elements);
	}

	public List<OperationCombatElement> getCombatElements(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getCombatElements();
	}

	public void setRoutes(Long id, @Valid List<OperationRoute> routes) {
		routes.forEach(route -> {
			route.getWaypoints().forEach(waypoint -> {
				if (waypoint.getHeight() == null) {
					waypoint.setHeight(0D);
				}
				if (StringUtils.isBlank(waypoint.getLabel())) {
					waypoint.setLabel(String.valueOf(waypoint.getNumber()));
				}
			});
		});
		operationRepository.setRoutesToOperation(id, routes);
	}

	public List<OperationRoute> getRoutes(Long id) throws BadRequestException {
		var operation = checkOperation(id);
		return operation.getRoutes();
	}

	private Person checkPerson(Long id) throws BadRequestException {
		return personRepository.findById(id).orElseThrow(() -> new BadRequestException(new ErrorMessage("Incorrect personid %s", id)));
	}
}
