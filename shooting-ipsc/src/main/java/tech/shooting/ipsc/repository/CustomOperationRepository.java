package tech.shooting.ipsc.repository;

import java.util.List;

import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Weather;

public interface CustomOperationRepository {

	public Weather setWeatherToOperation(Long operationId, Weather weather);

	public void setParticipantsToOperation(Long operationId, List<OperationParticipant> list);

	public void setSymbolsToOperation(Long operationId, List<OperationSymbol> list);

	public void setMainIndicatorsToOperation(Long operationId, List<OperationMainIndicator> list);
}
