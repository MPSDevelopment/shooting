package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Competition;

@Repository
public interface CompetitionRepository extends MongoRepository<Competition, Long> {
}
