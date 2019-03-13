package tech.shooting.ipsc.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Stage;

public class CompetitionDao {
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public Stage getStageById(Long id) {
		Criteria elementMatchCriteria = Criteria.where("competition.stages").elemMatch(Criteria.where("id").is(id));
		Query query = Query.query(elementMatchCriteria);
		query.fields().position("competition.stages", 1);
		Stage stage = mongoOperations.findOne(query, Stage.class);
		return stage;
	}
	
    public void pushStageToCompetition(Long competitionId, Stage stage) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(competitionId)), new Update().push("stages", stage), Competition.class);
    }

    public void pullStageFromCompetition(Long competitionId, Stage stage) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(competitionId)), new Update().pull("stages", stage), Competition.class); 
    }
}
