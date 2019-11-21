package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CustomPersonRepository {

	public List<Person> findByDivisionId(Long id);

	public List<Division> findByDivisionIdRecursive(Long id);
	
	public Page<Person> getPersonListByPage(List<Division> divisionList, PageRequest pageable);
}
