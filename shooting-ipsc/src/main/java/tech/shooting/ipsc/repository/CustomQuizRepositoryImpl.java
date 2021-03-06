package tech.shooting.ipsc.repository;

import com.mpsdevelopment.plasticine.commons.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import tech.shooting.ipsc.pojo.Question;
import tech.shooting.ipsc.pojo.Quiz;

public class CustomQuizRepositoryImpl implements CustomQuizRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void pullQuestion (Long quizId, Long questionId) {
		Query query = Query.query(Criteria.where(Quiz.ID_FIELD).is(quizId));
		Update pull = new Update().pull(Quiz.QUESTIONS, Query.query(Criteria.where(Quiz.ID_FIELD).is(questionId)));
		mongoTemplate.updateFirst(query, pull, Quiz.class);
	}

	@Override
	public Question pushQuestionToQuiz (Long quizId, Question question) {
		if(question.getId() == null) {
			question.setId(IdGenerator.nextId());
		}
		mongoTemplate.updateFirst(Query.query(Criteria.where(Quiz.ID_FIELD).is(quizId)), new Update().push(Quiz.QUESTIONS, question), Quiz.class);
		return question;
	}
}
