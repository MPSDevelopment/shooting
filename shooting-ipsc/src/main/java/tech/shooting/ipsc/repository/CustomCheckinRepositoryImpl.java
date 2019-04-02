package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CustomCheckinRepositoryImpl implements CustomCheckinRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	private Set<Long> divisions = new HashSet<>();

	@Override
	public List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Division division) {
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(5);
		Query query = new Query(Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		query.addCriteria(Criteria.where(CheckIn.DIVISION_ID).is(division.getId()));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Division divisionId) {
		addedChild(divisionId);
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(20);
		Query query = new Query(Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		log.info("Division id: %s", divisions);
		query.addCriteria(Criteria.where(CheckIn.DIVISION_ID).in(divisions));
		return mongoTemplate.find(query, CheckIn.class);
	}

	private void addedChild (Division divisionId) {
		divisions.add(divisionId.getId());
		if(divisionId.getChildren().size() == 0 || divisionId.getChildren().size() == 1 && divisionId.getChildren().get(0) == null) {
			return;
		}
		for(Division d : divisionId.getChildren()) {
			addedChild(d);
		}
	}

	@Override
	public List<CheckIn> findAllByDivision (Long division) {
		Query query = new Query(Criteria.where(CheckIn.DIVISION_ID).is(division));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDate (OffsetDateTime createdDate) {
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(5);
		Query query = new Query(Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		return mongoTemplate.find(query, CheckIn.class);
	}
}
