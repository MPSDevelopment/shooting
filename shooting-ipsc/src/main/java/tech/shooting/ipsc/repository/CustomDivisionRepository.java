package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;

public interface CustomDivisionRepository {
	Division updateDivisionName (Long id, String name);
}
