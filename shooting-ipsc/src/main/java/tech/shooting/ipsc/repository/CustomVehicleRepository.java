package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Vehicle;

import java.util.List;

public interface CustomVehicleRepository {

	List<Vehicle> findByPersonDivision(Division division);
}
