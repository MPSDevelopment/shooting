package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Subject;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, Long>, CustomSubjectRepository {
}
