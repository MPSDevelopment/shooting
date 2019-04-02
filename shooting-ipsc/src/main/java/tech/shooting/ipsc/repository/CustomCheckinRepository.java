package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.CheckIn;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomCheckinRepository {
	List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Long divisionId);

	List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Long divisionId);
}
