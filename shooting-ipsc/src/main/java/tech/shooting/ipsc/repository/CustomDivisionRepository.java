package tech.shooting.ipsc.repository;

import java.util.List;

import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Stage;

public interface CustomDivisionRepository  {

	public List<Division> findByDivisionId(Long id);

}
