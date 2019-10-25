package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.bean.StandardScoreRequest;
import tech.shooting.ipsc.pojo.StandardScore;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomStandardScoreRepository {
	
	public List<StandardScore> getScoreList(StandardScoreRequest request);
	
	public Page<StandardScore> getScoreList(StandardScoreRequest query, PageRequest pageable);
	
}
