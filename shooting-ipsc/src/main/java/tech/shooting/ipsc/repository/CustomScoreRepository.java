package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Score;
import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

public interface CustomScoreRepository {
	
	List<Score> getAggregatedScoreList();
}
