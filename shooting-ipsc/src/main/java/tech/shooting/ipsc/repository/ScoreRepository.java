package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Score;

import java.util.List;

@Repository
public interface ScoreRepository extends MongoRepository<Score, Long> {

	List<Score> findAllByPersonId(Long personId);

	List<Score> findAllByStageId(Long stageId);

	List<Score> findByStageIdIn(List<Long> stageId);

	Score findByPersonIdAndStageId(Long personId, Long stageId);
}
