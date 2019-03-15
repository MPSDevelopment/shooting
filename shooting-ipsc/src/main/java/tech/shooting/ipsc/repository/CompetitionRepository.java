package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;

@Repository
public interface CompetitionRepository extends MongoRepository<Competition, Long>,CustomCompetitionRepository {
	
	public Competition findByName (String name);
	
	@Query(value="{ 'stages.id' : ?0 }", fields="{ 'stages.$' : 1 }")
	public Stage findByStageId(Long stageId);
}
