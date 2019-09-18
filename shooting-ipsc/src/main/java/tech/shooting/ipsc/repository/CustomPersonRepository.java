package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

public interface CustomPersonRepository {

	public List<Person> findByDivisionId(Long id);

	public List<Division> findByDivisionIdRecursive(Long id);
}
