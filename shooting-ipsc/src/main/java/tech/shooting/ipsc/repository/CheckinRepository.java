package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.CheckIn;
import tech.shooting.ipsc.pojo.User;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CheckinRepository extends MongoRepository<CheckIn, Long>, CustomCheckinRepository {
	List<CheckIn> findAllByCreatedDateAndOfficer (OffsetDateTime createdDate, User officer);
}
