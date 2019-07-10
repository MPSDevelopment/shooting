package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.WorkSpace;

@Repository
public interface WorkSpaceRepository extends MongoRepository<WorkSpace, Long> {
}
