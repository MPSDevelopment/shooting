package tech.shooting.ipsc.repository;

import java.util.List;

import tech.shooting.ipsc.pojo.OperationCombatElement;
import tech.shooting.ipsc.pojo.OperationCommandantService;
import tech.shooting.ipsc.pojo.OperationMainIndicator;
import tech.shooting.ipsc.pojo.OperationParticipant;
import tech.shooting.ipsc.pojo.OperationRoute;
import tech.shooting.ipsc.pojo.OperationSignal;
import tech.shooting.ipsc.pojo.OperationSymbol;
import tech.shooting.ipsc.pojo.Weather;

public interface CustomOperationRepository {

	public Weather setWeatherToOperation(Long operationId, Weather weather);

	public void setParticipantsToOperation(Long operationId, List<OperationParticipant> list);

	public void setSymbolsToOperation(Long operationId, List<OperationSymbol> list);

	public void setMainIndicatorsToOperation(Long operationId, List<OperationMainIndicator> list);

	public void setCombatSignalsToOperation(Long operationId, List<OperationSignal> list);

	public void setCommandantServicesToOperation(Long operationId, List<OperationCommandantService> list);

	public void setCombatElementsToOperation(Long id, List<OperationCombatElement> elements);
	
	public void setRoutesToOperation(Long id, List<OperationRoute> routes);
}
