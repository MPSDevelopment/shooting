package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Slf4j
class CustomPersonRepositoryImpl implements CustomPersonRepository {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Person> findByDivisionId (Long id) {
		//		GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder().from("devision").startWith("parent").connectFrom("parent").connectTo("children").restrict(Criteria.where("id").is(id)).as
		//		("divisions");
		//		Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("to.refId").is(id)), graphLookupOperation);
		//
		//		TypedAggregation<Division> aggregationPipeline = Aggregation.newAggregation(Division.class, graphLookupOperation);
		//		List<Division> threadedPosts =  mongoTemplate.aggregate(aggregationPipeline, Division.class).getMappedResults();
		return null;
	}
}
