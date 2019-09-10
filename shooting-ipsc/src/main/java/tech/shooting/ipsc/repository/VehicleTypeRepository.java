package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.VehicleTypeBean;
import tech.shooting.ipsc.bean.WeaponTypeBean;
import tech.shooting.ipsc.pojo.VehicleType;
import tech.shooting.ipsc.pojo.WeaponType;


@Repository
public interface VehicleTypeRepository extends MongoRepository<VehicleType,Long> {
	
	VehicleType findByName(VehicleTypeBean bean);
}
