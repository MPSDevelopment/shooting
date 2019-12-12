package tech.shooting.ipsc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.bean.OperationCombatListHeaderBean;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Info;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.pojo.WeaponType;
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

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = OperationRepository.class)
@ContextConfiguration(classes = { OperationService.class, IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class OperationServiceTest {

	@Autowired
	private OperationService operationService;

	@Autowired
	private OperationRepository operationRepository;
	
	@Autowired
	private PersonRepository personRepository;

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

	private List<OperationCombatListHeaderBean> headers;

	private Operation testOperation;

	private List<List<String>> combatListData;

	@BeforeEach
	public void beforeEach() {
		operationService.clearTypes();
		operationService.clearObjects();

		operationRepository.deleteAll();

		testOperation = new Operation().setInfo(new Info().setNamedRus("Test"));
	}

	@Test
	public void getHeaders() {
		headers = operationService.getHeaders();
		assertEquals(0, headers.size());

		weaponTypeRepository.save(new WeaponType().setName("AK-47"));
		weaponTypeRepository.save(new WeaponType().setName("AK-74"));

		headers = operationService.getHeaders();
		assertEquals(2, headers.size());
	}

	@Test
	public void getCombatListData() throws BadRequestException {
		testOperation = operationRepository.save(testOperation);
		combatListData = operationService.getCombatListData(testOperation.getId(), operationService.getHeaders());
		assertEquals(0, combatListData.size());
		
		var testPerson = personRepository.save(new Person().setName("Thor"));
		testOperation.setParticipants(Arrays.asList(new OperationParticipant().setPerson(testPerson)));
		
		operationRepository.save(testOperation);
		
		combatListData = operationService.getCombatListData(testOperation.getId(), operationService.getHeaders());
		assertEquals(0, combatListData.size());
		
		var weaponType = weaponTypeRepository.save(new WeaponType().setName("AK-47"));
		weaponTypeRepository.save(new WeaponType().setName("AK-74"));
		
		headers = operationService.getHeaders();
		assertEquals(2, headers.size());
		
		weaponRepository.save(new Weapon().setWeaponType(weaponType).setOwner(testPerson).setSerialNumber("123"));
		
		combatListData = operationService.getCombatListData(testOperation.getId(), operationService.getHeaders());
		
		log.info("Data is %s", JacksonUtils.getJson(combatListData));
		
		assertEquals(1, combatListData.size());
		assertEquals(2, combatListData.get(0).size());		
		assertEquals(Arrays.asList( "1", "0"), combatListData.get(0));
	}

}
