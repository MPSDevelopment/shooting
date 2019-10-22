package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.pojo.QuizScore;
import java.util.List;

public interface CustomQuizScoreRepository {
	
	public List<QuizScore> getScoreList(QuizScoreRequest request);
	
}
