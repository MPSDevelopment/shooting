package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.pojo.Subject;

import java.util.List;

public interface CustomRankRepository {
	
	List<Rank> createIfNotExists (List<Rank> ranks);
}
