package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;

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
import tech.shooting.commons.constraints.IpscConstants;
import tech.shooting.ipsc.config.IpscMongoConfig;
import tech.shooting.ipsc.pojo.Info;
import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationCombatElement;
import tech.shooting.ipsc.pojo.OperationCommandantService;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationRoute;
import tech.shooting.ipsc.pojo.OperationSignal;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weather;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableMongoRepositories(basePackageClasses = OperationRepository.class)
@ContextConfiguration(classes = { IpscMongoConfig.class })
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
@DirtiesContext
@Tag(IpscConstants.UNIT_TEST_TAG)
public class OperationRepositoryTest {
	
	@Autowired
	private OperationRepository operationRepository;

	private Operation operation;

	private Weather weather;

	@Autowired
	private PersonRepository personRepository;

	@BeforeEach
	public void before() {
		operationRepository.deleteAll();
		operation = new Operation().setInfo(new Info().setNamedRus("Test name"));
		operationRepository.save(operation);
		
		weather = new Weather().setTemperature(100F);
	}

	@Test
	public void setWeatherToOperation() {
		assertNull(operationRepository.findById(operation.getId()).get().getWeather());
		operationRepository.setWeatherToOperation(operation.getId(), weather);
		assertNotNull(operationRepository.findById(operation.getId()).get().getWeather());
		assertEquals(100F, operationRepository.findById(operation.getId()).get().getWeather().getTemperature());
	}
	
	@Test
	public void setParticipantsToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getParticipants().size());
		operationRepository.setParticipantsToOperation(operation.getId(), Arrays.asList(new Person()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getParticipants());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getParticipants().size());
	}
	
	@Test
	public void setSymbolsToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getSymbols().size());
		operationRepository.setSymbolsToOperation(operation.getId(), Arrays.asList(new OperationSymbol()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getSymbols());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getSymbols().size());
	}
	
	@Test
	public void setMainIndicatorsToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getMainIndicators().size());
		operationRepository.setMainIndicatorsToOperation(operation.getId(), Arrays.asList(new OperationMainIndicator()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getMainIndicators());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getMainIndicators().size());
	}
	
	@Test
	public void setSignalsToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getSignals().size());
		operationRepository.setCombatSignalsToOperation(operation.getId(), Arrays.asList(new OperationSignal()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getSignals());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getSignals().size());
	}
	
	@Test
	public void setCommandantServicesToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getCommandantServices().size());
		operationRepository.setCommandantServicesToOperation(operation.getId(), Arrays.asList(new OperationCommandantService()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getCommandantServices());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getCommandantServices().size());
	}
	
	@Test
	public void setCombatElementsToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getCombatElements().size());
		operationRepository.setCombatElementsToOperation(operation.getId(), Arrays.asList(new OperationCombatElement()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getCombatElements());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getCombatElements().size());
	}
	
	@Test
	public void setRoutesToOperation() {
		assertEquals(0, operationRepository.findById(operation.getId()).get().getRoutes().size());
		operationRepository.setRoutesToOperation(operation.getId(), Arrays.asList(new OperationRoute()));
		assertNotNull(operationRepository.findById(operation.getId()).get().getRoutes());
		assertEquals(1, operationRepository.findById(operation.getId()).get().getRoutes().size());
	}
}
