package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.pojo.QuizScore;

@Repository
public interface QuizScoreRepository extends MongoRepository<QuizScore, Long>, CustomQuizScoreRepository {
	
}
