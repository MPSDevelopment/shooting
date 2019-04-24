package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.pojo.Rank;
@Repository
public interface RankRepository extends MongoRepository<Rank, Long>, CustomRankRepository {
	
	public Rank findByRus(String rus);
	
}
