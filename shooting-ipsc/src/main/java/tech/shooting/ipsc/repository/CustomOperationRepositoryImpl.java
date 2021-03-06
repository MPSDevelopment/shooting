package tech.shooting.ipsc.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationCombatElement;
import tech.shooting.ipsc.pojo.OperationCommandantService;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationRoute;
import tech.shooting.ipsc.pojo.OperationSignal;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weather;

public class CustomOperationRepositoryImpl implements CustomOperationRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Weather setWeatherToOperation(Long operationId, Weather weather) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.WEATHER_FIELD, weather), Operation.class);
		return weather;
	}

	@Override
	public void setParticipantsToOperation(Long operationId, List<Person> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.PARTICIPANTS_FIELD, list), Operation.class);
	}
	
	@Override
	public void setSymbolsToOperation(Long operationId, List<OperationSymbol> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.SYMBOLS_FIELD, list), Operation.class);
	}
	
	@Override
	public void setMainIndicatorsToOperation(Long operationId, List<OperationMainIndicator> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.MAIN_INDICATORS_FIELD, list), Operation.class);
	}

	@Override
	public void setCombatSignalsToOperation(Long operationId, List<OperationSignal> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.SIGNALS_FIELD, list), Operation.class);
	}

	@Override
	public void setCommandantServicesToOperation(Long operationId, List<OperationCommandantService> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.COMMANDANT_SERVICES_FIELD, list), Operation.class);
	}
	
	public void setCombatElementsToOperation(Long operationId, List<OperationCombatElement> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.COMBAT_ELEMENTS_FIELD, list), Operation.class);
	}
	
	public void setRoutesToOperation(Long operationId, List<OperationRoute> list) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Operation.ID_FIELD).is(operationId)), new Update().set(Operation.ROUTES_FIELD, list), Operation.class);
	}
}
