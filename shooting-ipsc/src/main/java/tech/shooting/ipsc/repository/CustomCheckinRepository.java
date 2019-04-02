package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.Division;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomCheckinRepository {
	List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Division division);

	List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Long divisionId);

	List<CheckIn> findAllByDivision (Long division);

	List<CheckIn> findAllByDate (OffsetDateTime createdDate);
}
