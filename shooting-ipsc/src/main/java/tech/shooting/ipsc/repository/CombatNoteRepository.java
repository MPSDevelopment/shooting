package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.shooting.ipsc.pojo.CombatNote;
import tech.shooting.ipsc.pojo.Division;

import java.util.List;

public interface CombatNoteRepository extends MongoRepository<CombatNote, Long> {
	List<CombatNote> findAllByDivision (Division division);
}
