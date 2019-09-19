package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Equipment;
import java.util.List;

public interface CustomEquipmentRepository {

	List<Equipment> findByPersonDivision(Division division);
}
