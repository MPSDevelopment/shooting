package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.bean.QuizScoreRequest;
import tech.shooting.ipsc.pojo.QuizScore;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomQuizScoreRepository {
	
	public List<QuizScore> getScoreList(QuizScoreRequest request);
	
	public Page<QuizScore> getScoreList(QuizScoreRequest query, PageRequest pageable);
	
}
