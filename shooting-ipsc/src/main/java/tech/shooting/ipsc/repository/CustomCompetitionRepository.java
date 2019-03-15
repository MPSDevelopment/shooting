package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;

public interface CustomCompetitionRepository  {

	Competition getByStageId(Long id);
	Stage getStageById(Long id);
	public void pushStageToCompetition(Long competitionId, Stage stage);
	public void pullStageFromCompetition(Long competitionId, Stage stage);

}
