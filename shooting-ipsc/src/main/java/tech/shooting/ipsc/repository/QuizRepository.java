package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.shooting.ipsc.enums.Subject;
import tech.shooting.ipsc.pojo.Quiz;

import java.util.List;

public interface QuizRepository extends MongoRepository<Quiz, Long> {
	List<Quiz> findBySubject (Subject subject);
}