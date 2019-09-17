package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.EquipmentTypeBean;
import tech.shooting.ipsc.pojo.EquipmentType;


@Repository
public interface EquipmentTypeRepository extends MongoRepository<EquipmentType,Long> {
	
	EquipmentType findByName(EquipmentTypeBean bean);
}
