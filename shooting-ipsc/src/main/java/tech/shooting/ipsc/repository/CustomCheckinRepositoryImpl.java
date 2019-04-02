package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import tech.shooting.ipsc.pojo.CheckIn;

import java.time.OffsetDateTime;
import java.util.List;

public class CustomCheckinRepositoryImpl implements CustomCheckinRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<CheckIn> findAllByDateAndDivision (OffsetDateTime createdDate, Long divisionId) {
		return null;
	}

	@Override
	public List<CheckIn> findAllByDateAndRootDivision (OffsetDateTime createdDate, Long divisionId) {
		return null;
	}
}
