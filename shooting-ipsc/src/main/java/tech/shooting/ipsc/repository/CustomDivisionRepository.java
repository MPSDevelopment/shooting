package tech.shooting.ipsc.repository;

import java.util.List;

import tech.shooting.ipsc.pojo.Division;

public interface CustomDivisionRepository {
	
	Division updateDivisionName (Long id, String name);
	
	Division createIfNotExists(Division division);
}
