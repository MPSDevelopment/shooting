package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.shooting.ipsc.pojo.CombatNote;

public interface CombatNoteRepository extends MongoRepository<CombatNote, Long> {
}
