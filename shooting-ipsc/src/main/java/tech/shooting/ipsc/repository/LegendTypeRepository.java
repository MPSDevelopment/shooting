package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.ipsc.bean.AmmoTypeBean;
import tech.shooting.ipsc.pojo.AmmoType;
import tech.shooting.ipsc.pojo.LegendType;

@Repository
public interface LegendTypeRepository extends MongoRepository<LegendType, Long> {

	LegendType findByName(String name);
}
