package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.CommunicationEquipment;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Vehicle;

import java.util.List;

public interface CustomCommunicationEquipmentRepository {

	List<CommunicationEquipment> findByPersonDivision(Division division);
}
