package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.pojo.CombatNote;
import tech.shooting.ipsc.pojo.Division;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class CustomCombatNoteRepositoryImpl implements CustomCombatNoteRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<CombatNote> findAllByDivisionAndDateAndInterval(Division division, String date, TypeOfInterval interval) {
        OffsetDateTime parse = OffsetDateTime.parse(date);
        MatchOperation match = getMatch(division, parse, interval);
        return mongoTemplate.aggregate(newAggregation(match), CombatNote.class, CombatNote.class).getMappedResults();
    }



    public static List<OffsetDateTime> timeInterval (OffsetDateTime date, TypeOfInterval interval) {
        List<OffsetDateTime> inter = new ArrayList<>();
        LocalDate localDate;
        ZoneOffset offset;
        switch(interval) {
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
        }
        return inter;
    }
    private MatchOperation getMatch (Division division,OffsetDateTime date, TypeOfInterval interval) {
        Criteria criteria = Criteria.where(CombatNote.DIVISION).is(division);
        List<OffsetDateTime> starEnd = timeInterval(date, interval);
        OffsetDateTime searchStart = starEnd.get(0);
        OffsetDateTime searchEnd = starEnd.get(1);
        criteria = criteria.andOperator(Criteria.where(BaseDocument.CREATED_DATE_FIELD).gte(searchStart).lte(searchEnd));
        return match(criteria);
    }
}
