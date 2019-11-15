package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Animal;
import tech.shooting.ipsc.pojo.Division;
import java.util.List;

public interface CustomAnimalRepository {

	List<Animal> findByPersonDivision(Division division);
}
