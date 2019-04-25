package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.bean.AggBean;
import tech.shooting.ipsc.bean.NameStatus;
import tech.shooting.ipsc.bean.Stat;
import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.enums.TypeOfPresence;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomCheckinRepository {
	List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Division division);

	List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Division divisionId);

	List<CheckIn> findAllByDivision (Long division);

	List<CheckIn> findAllByDate (OffsetDateTime createdDate);

	List<AggBean> findAllByDivisionStatusDateInterval (Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval);

	List<CheckIn> findAllByStatus (TypeOfPresence status);

	List<OffsetDateTime>  timeInterval (OffsetDateTime date, TypeOfInterval interval);

	List<Stat> getCombatNoteByDivisionFromPeriod (Division division, OffsetDateTime dateTime, TypeOfInterval interval);

	List<NameStatus> findAllByDivisionDateInterval (Division checkDivision, OffsetDateTime date, TypeOfInterval interval);
}
