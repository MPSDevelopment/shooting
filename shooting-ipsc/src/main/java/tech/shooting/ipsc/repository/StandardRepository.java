package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Standard;

@Repository
public interface StandardRepository extends MongoRepository<Standard,Long> {
}
