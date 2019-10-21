package tech.shooting.ipsc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.pojo.Subject;

import java.util.ArrayList;
import java.util.List;

public class CustomRankRepositoryImpl implements CustomRankRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Rank> createIfNotExists (List<Rank> ranks) {
		List<Rank> res = new ArrayList<>();
		for(int i = 0; i < ranks.size(); i++) {
			Query query = Query.query(Criteria.where(Rank.KZ).is(ranks.get(i).getKz()));
			query.addCriteria(Criteria.where(Rank.RUS).is(ranks.get(i).getRus()));
			if(mongoTemplate.find(query, Rank.class).size() == 0 || mongoTemplate.find(query, Rank.class) == null) {
				res.add(mongoTemplate.save(ranks.get(i)));
			}
		}
		return res;
	}
}
