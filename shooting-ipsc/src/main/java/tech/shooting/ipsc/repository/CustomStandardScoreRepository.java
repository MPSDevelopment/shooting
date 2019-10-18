package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.pojo.StandardScore;
import java.util.List;

public interface CustomStandardScoreRepository {
	
	public List<StandardScore> getScoreList(StandardScoreRequest request);
	
}
