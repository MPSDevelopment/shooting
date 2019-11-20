package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Operation;

@Repository
public interface OperationRepository extends MongoRepository<Operation, Long>, CustomOperationRepository {

}
