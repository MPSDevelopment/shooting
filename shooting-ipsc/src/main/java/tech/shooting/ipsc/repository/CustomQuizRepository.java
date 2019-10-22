package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Question;

public interface CustomQuizRepository {
	
	void pullQuestion (Long quizId, Long questionId);

	Question pushQuestionToQuiz (Long quizId, Question question);
}
