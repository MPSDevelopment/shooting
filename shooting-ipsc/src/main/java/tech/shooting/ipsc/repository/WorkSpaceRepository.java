package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Workspace;

@Repository
public interface WorkSpaceRepository extends MongoRepository<Workspace, Long> {
}
