package tech.shooting.ipsc.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import tech.shooting.ipsc.pojo.Operation;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.OperationSymbol;
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
	public void setParticipantsToOperation(Long operationId, List<OperationParticipant> list) {
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
}
