package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;

import java.util.List;

public interface CustomDivisionRepository {
	public List<Division> findByDivisionId (Long id);
}
