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
	
	public Stage getStageById(Long competitionId, Long id) {
		Query query = Query.query(Criteria.where("competition.stages").elemMatch(Criteria.where("id").is(id)));
		query.fields().position("stages", 1);
		Stage stage = mongoTemplate.findOne(query, Stage.class);
		return stage;

//		Query query = new Query();
//		query.addCriteria(Criteria.where("id").is(competitionId));
//		query.fields().elemMatch("stages", Criteria.where("id").is(id));
//		return mongoTemplate.findOne(query, Stage.class);
	}

	public void pushStageToCompetition(Long competitionId, Stage stage) {
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(competitionId)), new Update().push("stages", stage), Competition.class);
	}

	public void pullStageFromCompetition(Long competitionId, Stage stage) {
		// mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(competitionId)), new Update().pull("stages", stage.getId()), Competition.class);
		mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(competitionId)), new Update().pull("stages", Query.query(Criteria.where("id").is(stage.getId()))), Competition.class);
	}
}
