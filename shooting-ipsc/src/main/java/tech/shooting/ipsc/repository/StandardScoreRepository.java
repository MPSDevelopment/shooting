package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.StandardScore;

import java.util.List;

@Repository
public interface StandardScoreRepository extends MongoRepository<StandardScore, Long>, CustomStandardScoreRepository {

	List<StandardScore> findAllByPersonId(Long personId);

	List<StandardScore> findAllByStandardId(Long standardId);

	List<StandardScore> findByStandardIdIn(List<Long> list);

	List<StandardScore> findByPersonIdAndStandardId(Long personId, Long standardId);
}
