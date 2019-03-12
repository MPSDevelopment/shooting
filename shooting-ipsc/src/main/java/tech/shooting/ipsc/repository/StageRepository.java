package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Stage;

@Repository
public interface StageRepository extends MongoRepository<Stage, Long> {
}
