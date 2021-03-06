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
import tech.shooting.ipsc.bean.AggBean;
import tech.shooting.ipsc.bean.NameStatus;
import tech.shooting.ipsc.bean.Stat;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
public class CustomCheckinRepositoryImpl implements CustomCheckinRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<CheckIn> findAllByDateAndDivision(OffsetDateTime createdDate, Division division) {
		OffsetDateTime startTime = createdDate.truncatedTo(ChronoUnit.DAYS);
		OffsetDateTime finishTime = createdDate.plusDays(1).truncatedTo(ChronoUnit.DAYS);

		Query query = new Query(where(BaseDocument.CREATED_DATE_FIELD).gte(startTime).lte(finishTime));
		query.addCriteria(where(CheckIn.DIVISION_ID).is(division.getId()));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDateAndRootDivision(Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) {
		
		date = Optional.ofNullable(date).orElse(OffsetDateTime.now());
		
		OffsetDateTime startTime = date.truncatedTo(ChronoUnit.DAYS);
		OffsetDateTime finishTime = date.plusDays(1).truncatedTo(ChronoUnit.DAYS);

		Query query = new Query(where(BaseDocument.CREATED_DATE_FIELD).gte(startTime).lte(finishTime));

		if (division != null) {
			Set<Long> divisions = new HashSet<>();
			addedChild(division, divisions);
			log.info("Division id: %s", divisions);
			query.addCriteria(where(CheckIn.DIVISION_ID).in(divisions));
		}
		
		
		if (status!=null) {
			query.addCriteria(Criteria.where(CheckIn.STATUS).is(status));
		}
		
		return mongoTemplate.find(query, CheckIn.class);
	}

	private void addedChild(Division divisionId, Set<Long> divisions) {
		divisions.add(divisionId.getId());
		if (divisionId.getChildren().size() == 0 || divisionId.getChildren().size() == 1 && divisionId.getChildren().get(0) == null) {
			return;
		}
		for (Division d : divisionId.getChildren()) {
			addedChild(d, divisions);
		}
	}

	@Override
	public List<CheckIn> findAllByDivision(Long division) {
		Query query = new Query(where(CheckIn.DIVISION_ID).is(division));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<CheckIn> findAllByDate(OffsetDateTime createdDate) {
		OffsetDateTime startTime = createdDate.truncatedTo(ChronoUnit.DAYS);
		OffsetDateTime finishTime = createdDate.plusDays(1).truncatedTo(ChronoUnit.DAYS);

		Query query = new Query(where(BaseDocument.CREATED_DATE_FIELD).gte(startTime).lte(finishTime));
		return mongoTemplate.find(query, CheckIn.class);
	}

	@Override
	public List<Stat> getCombatNoteByDivisionFromPeriod(Division division, OffsetDateTime dateTime, TypeOfInterval interval) {
		MatchOperation match = getMatch(dateTime, interval);
		GroupOperation groupOperation = group("status").last("status").as("status").count().as("count");
		return mongoTemplate.aggregate(newAggregation(match, groupOperation), CheckIn.class, Stat.class).getMappedResults();
	}

	@Override
	public List<NameStatus> findAllByDivisionDateInterval(Division checkDivision, OffsetDateTime date, TypeOfInterval interval) {
		GroupOperation groupOperation = group("person").last("person").as("person").first("status").as("status").first("createdDate").as("date");
		ProjectionOperation projectionOperation = project("status", "date").and("person").previousOperation();
		return mongoTemplate.aggregate(newAggregation(getMatchOperation(date, interval, checkDivision, TypeOfPresence.ALL), groupOperation, projectionOperation), CheckIn.class, NameStatus.class).getMappedResults();
	}

	private MatchOperation getMatch(OffsetDateTime date, TypeOfInterval interval) {
		Criteria criteria;
		List<OffsetDateTime> starEnd = timeInterval(date, interval);
		OffsetDateTime searchStart = starEnd.get(0);
		OffsetDateTime searchEnd = starEnd.get(1);
		criteria = Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd);
		return match(criteria);
	}

	@Override
	public List<AggBean> findAllByDivisionStatusDateInterval(Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval) {
		
		date = Optional.ofNullable(date).orElse(OffsetDateTime.now());
		
		GroupOperation groupOperation = group("person").last("person").as("person").addToSet("status").as("stat");
		ProjectionOperation projectionOperation = project("stat").and("person").previousOperation();
		return mongoTemplate.aggregate(newAggregation(getMatchOperation(date, interval, division, status), groupOperation, projectionOperation), CheckIn.class, AggBean.class).getMappedResults();
	}

	private MatchOperation getMatchOperation(OffsetDateTime date, TypeOfInterval interval, Division division, TypeOfPresence status) {
		
		date = Optional.ofNullable(date).orElse(OffsetDateTime.now());
		
		LocalDate localDate;
		LocalTime localTime;
		ZoneOffset offset;
		List<OffsetDateTime> starEnd = timeInterval(date, interval);
		OffsetDateTime searchStart = starEnd.get(0);
		OffsetDateTime searchEnd = starEnd.get(1);
		Set<Long> divisions = new HashSet<>();
		addedChild(division, divisions);
		Criteria priceCriteria;
		if (status.equals(TypeOfPresence.ALL)) {
			priceCriteria = where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd).andOperator(where(CheckIn.DIVISION_ID).in(divisions));
		} else {
			priceCriteria = where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd).andOperator(where(CheckIn.DIVISION_ID).in(divisions).andOperator(where(CheckIn.STATUS).is(status)));
		}
		return match(priceCriteria);
	}

	@Override
	public List<OffsetDateTime> timeInterval(OffsetDateTime date, TypeOfInterval interval) {
		
		date = Optional.ofNullable(date).orElse(OffsetDateTime.now());
		
		List<OffsetDateTime> inter = new ArrayList<>();
		LocalDate localDate;
		ZoneOffset offset;

		interval = Optional.ofNullable(interval).orElse(TypeOfInterval.DAY);
		switch (interval) {
		case MORNING:
			localDate = date.toLocalDate();
			offset = date.getOffset();
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.MORNING.getStart(), offset));
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.MORNING.getEnd(), offset));
			break;
		case EVENING:
			localDate = date.toLocalDate();
			offset = date.getOffset();
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.EVENING.getStart(), offset));
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.EVENING.getEnd(), offset));
			break;
		case DAY:
			localDate = date.toLocalDate();
			offset = date.getOffset();
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.DAY.getStart(), offset));
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.DAY.getEnd(), offset));
			break;
		case WEEK:
			localDate = date.toLocalDate();
			offset = date.getOffset();
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.WEEK.getStart(), offset));
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.WEEK.getEnd(), offset).plusDays(7));
			break;
		case MONTH:
			localDate = date.toLocalDate();
			offset = date.getOffset();
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.MONTH.getStart(), offset));
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.MONTH.getEnd(), offset).plusMonths(1));
			break;
		default:
			localDate = date.toLocalDate();
			offset = date.getOffset();
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.DAY.getStart(), offset));
			inter.add(OffsetDateTime.of(localDate, TypeOfInterval.DAY.getEnd(), offset));
			break;
		}
		return inter;
	}

	@Override
	public List<CheckIn> findAllByStatus(TypeOfPresence status) {
		Query query = new Query(where(CheckIn.STATUS).is(status));
		return mongoTemplate.find(query, CheckIn.class);
	}
}
