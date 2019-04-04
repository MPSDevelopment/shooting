package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.shooting.ipsc.pojo.Quiz;
import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

public interface QuizRepository extends MongoRepository<Quiz, Long>, CustomQuizRepository {
	List<Quiz> findBySubject (Subject subject);

	List<Quiz> findBySubject (Long subject);
}
