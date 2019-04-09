package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, Long> {
}
