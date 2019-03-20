package tech.shooting.ipsc.repository;

import java.util.List;

import tech.shooting.ipsc.pojo.Competition;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Stage;

public interface CustomPersonRepository  {

	public List<Person> findByDivisionId(Long id);

}
