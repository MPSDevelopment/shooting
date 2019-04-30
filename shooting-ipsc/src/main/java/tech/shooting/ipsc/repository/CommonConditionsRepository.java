package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.CommonConditions;

@Repository
public interface CommonConditionsRepository extends MongoRepository<CommonConditions,Long> {
}
