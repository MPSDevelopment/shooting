package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;

import java.time.OffsetDateTime;
import java.util.List;

public class CustomCheckinRepositoryImpl implements CustomCheckinRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Division division) {
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(5);
		Query query = new Query(Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		query.addCriteria(Criteria.where(CheckIn.DIVISION_ID).is(division.getId()));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Long divisionId) {
		return null;
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
