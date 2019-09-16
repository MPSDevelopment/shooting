package tech.shooting.ipsc.repository;

import tech.shooting.ipsc.pojo.AnimalType;

import java.util.List;

public interface CustomAnimalTypeRepository {
	
	AnimalType createIfNotExists (AnimalType type);
}
