package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.QuizScore;

@Repository
public interface QuizScoreRepository extends MongoRepository<QuizScore, Long>, CustomQuizScoreRepository {
	
}
