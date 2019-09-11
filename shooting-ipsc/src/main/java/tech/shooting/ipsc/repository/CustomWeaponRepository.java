package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Weapon;

import java.util.List;

public interface CustomWeaponRepository {

	List<Weapon> findByPersonDivision(Division division);
}
