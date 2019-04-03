package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
public class CustomCheckinRepositoryImpl implements CustomCheckinRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Division division) {
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(5);
		Query query = new Query(where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		query.addCriteria(where(CheckIn.DIVISION_ID).is(division.getId()));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Division divisionId) {
		Set<Long> divisions = new HashSet<>();
		addedChild(divisionId, divisions);
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(20);
		Query query = new Query(where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		log.info("Division id: %s", divisions);
		query.addCriteria(where(CheckIn.DIVISION_ID).in(divisions));
		return mongoTemplate.find(query, CheckIn.class);
	}

	private void addedChild (Division divisionId, Set<Long> divisions) {
		divisions.add(divisionId.getId());
		if(divisionId.getChildren().size() == 0 || divisionId.getChildren().size() == 1 && divisionId.getChildren().get(0) == null) {
			return;
		}
		for(Division d : divisionId.getChildren()) {
			addedChild(d, divisions);
		}
	}

	@Override
	public List<CheckIn> findAllByDivision (Long division) {
		Query query = new Query(where(CheckIn.DIVISION_ID).is(division));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDate (OffsetDateTime createdDate) {
		OffsetDateTime offsetDateTime = createdDate.plusMinutes(5);
		Query query = new Query(where(BaseDocument.CREATED_DATE_FIELD).gte(createdDate).lte(offsetDateTime));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDivisionStatusDateInterval (Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) {
		Query query = new Query();
		LocalDate localDate;
		LocalTime localTime;
		ZoneOffset offset;
		OffsetDateTime searchStart = null;
		OffsetDateTime searchEnd = null;
		//setup range time for search
		switch(interval) {
			case MORNING:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.MORNING.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.MORNING.getEnd(), offset);
				break;
			case EVENING:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.EVENING.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.EVENING.getEnd(), offset);
				break;
			case DAY:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.DAY.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.DAY.getEnd(), offset);
				break;
			case WEEK:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.WEEK.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.WEEK.getEnd(), offset).plusDays(7);
				break;
			case MONTH:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.MONTH.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.MONTH.getEnd(), offset).plusMonths(1);
				break;
		}
		query.addCriteria(Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd));
		//added division's to list for find
		Set<Long> divisions = new HashSet<>();
		addedChild(division, divisions);
		query.addCriteria(where(CheckIn.DIVISION_ID).in(divisions));
		//switch vy status
		if(!status.equals(TypeOfPresence.ALL)) {
			query.addCriteria(where(CheckIn.STATUS).is(status));
		}
		List<CheckIn> mappedResults = mongoTemplate.aggregate(newAggregation(getMatchOperation(date, interval, division, status), getGroupOperation(), getProjectOperation()), CheckIn.class).getMappedResults();
		log.info("Result %s \n\n", mappedResults);
		return mongoTemplate.find(query, CheckIn.class);
	}

	private MatchOperation getMatchOperation (OffsetDateTime date, TypeOfInterval interval, Division division, TypeOfPresence status) {
		LocalDate localDate;
		LocalTime localTime;
		ZoneOffset offset;
		OffsetDateTime searchStart = null;
		OffsetDateTime searchEnd = null;
		switch(interval) {
			case MORNING:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.MORNING.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.MORNING.getEnd(), offset);
				break;
			case EVENING:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.EVENING.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.EVENING.getEnd(), offset);
				break;
			case DAY:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.DAY.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.DAY.getEnd(), offset);
				break;
			case WEEK:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.WEEK.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.WEEK.getEnd(), offset).plusDays(7);
				break;
			case MONTH:
				localDate = date.toLocalDate();
				offset = date.getOffset();
				searchStart = OffsetDateTime.of(localDate, TypeOfInterval.MONTH.getStart(), offset);
				searchEnd = OffsetDateTime.of(localDate, TypeOfInterval.MONTH.getEnd(), offset).plusMonths(1);
				break;
		}
		Set<Long> divisions = new HashSet<>();
		addedChild(division, divisions);
		Criteria priceCriteria;
		if(!status.equals(TypeOfPresence.ALL)) {
			priceCriteria = where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd).andOperator(where(CheckIn.DIVISION_ID).in(divisions).andOperator(where(CheckIn.STATUS).is(status)));
		}
		priceCriteria = where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd).andOperator(where(CheckIn.DIVISION_ID).in(divisions));
		return match(priceCriteria);
	}

	private GroupOperation getGroupOperation () {
		return group("person");
	}

	private ProjectionOperation getProjectOperation () {
		return project("person");
	}
	@Override
	public List<CheckIn> findAllByStatus (TypeOfPresence status) {
		Query query = new Query(where(CheckIn.STATUS).is(status));
		return mongoTemplate.find(query, CheckIn.class);
	}
}
