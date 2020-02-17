package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.enums.TypeOfInterval;
import tech.shooting.ipsc.pojo.CombatNote;
import tech.shooting.ipsc.pojo.Division;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomCombatNoteRepository {
	
    List<CombatNote> findAllByDivisionAndDateAndInterval(Division division, OffsetDateTime date,  TypeOfInterval interval);
}
