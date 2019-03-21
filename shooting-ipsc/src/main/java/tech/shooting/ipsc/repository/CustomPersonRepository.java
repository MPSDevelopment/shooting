package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Person;

import java.util.List;

public interface CustomPersonRepository {
	public List<Person> findByDivisionId (Long id);
}
