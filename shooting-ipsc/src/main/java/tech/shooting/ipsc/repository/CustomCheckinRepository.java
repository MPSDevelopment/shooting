package tech.shooting.ipsc.repository;

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

	List<CheckIn> findAllByDivisionStatusDateInterval (Division division, TypeOfPresence status, OffsetDateTime date, TypeOfInterval interval);

	List<CheckIn> findAllByStatus (TypeOfPresence status);
}
