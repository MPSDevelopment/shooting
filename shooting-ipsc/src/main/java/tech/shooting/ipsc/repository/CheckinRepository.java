package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.CheckIn;

@Repository
public interface CheckinRepository extends MongoRepository<CheckIn, Long> {
}
