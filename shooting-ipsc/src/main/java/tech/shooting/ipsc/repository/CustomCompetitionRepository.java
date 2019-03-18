package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;

public interface CustomCompetitionRepository  {

	public Competition getByStageId(Long id);
	
	public Stage getStageById(Long id);
	
	public Stage pushStageToCompetition(Long competitionId, Stage stage);
	
	public void pullStageFromCompetition(Long competitionId, Stage stage);

}
