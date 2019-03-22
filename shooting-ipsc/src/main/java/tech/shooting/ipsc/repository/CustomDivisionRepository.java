package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;

import java.util.List;

public interface CustomDivisionRepository {
	List<Division> findByDivisionId (Long id);

	Division updateDivisionName (Long id, String name);
}
