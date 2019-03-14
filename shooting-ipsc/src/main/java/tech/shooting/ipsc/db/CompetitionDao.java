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
	private MongoTemplate mongoTemplate;

	public Stage getStageById(Long id) {
		Query query = Query.query(Criteria.where(Competition.STAGES_FIELD + "." + Competition.ID_FIELD).is(id));
		query.fields().include(Competition.ID_FIELD).include(Competition.NAME_FIELD).position(Competition.STAGES_FIELD, 1);
		Competition competition = mongoTemplate.findOne(query, Competition.class);
		return competition == null ? null : competition.getStages().get(0);
	}

	public Competition getByStageId(Long id) {
		Query query = Query.query(Criteria.where(Competition.STAGES_FIELD + "." + Competition.ID_FIELD).is(id));
		return mongoTemplate.findOne(query, Competition.class);
	}

	public void pushStageToCompetition(Long competitionId, Stage stage) {
		mongoTemplate.updateFirst(Query.query(Criteria.where(Competition.ID_FIELD).is(competitionId)), new Update().push(Competition.STAGES_FIELD, stage), Competition.class);
	}

	public void pullStageFromCompetition(Long competitionId, Stage stage) {
		// mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(competitionId)), new Update().pull("stages", stage.getId()), Competition.class);
		mongoTemplate.updateFirst(Query.query(Criteria.where(Competition.ID_FIELD).is(competitionId)), new Update().pull(Competition.STAGES_FIELD, Query.query(Criteria.where(Competition.ID_FIELD).is(stage.getId()))), Competition.class);
	}
}
