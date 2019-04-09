package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.QuizReport;

@Repository
public interface ReportRepository extends MongoRepository<QuizReport, Long> {
}
