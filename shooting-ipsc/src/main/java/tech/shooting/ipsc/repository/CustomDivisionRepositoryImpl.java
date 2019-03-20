package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

@Slf4j
class CustomDivisionRepositoryImpl implements CustomDivisionRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Division> findByDivisionId(Long id) {

		// GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder().from("division").startWith("parent").connectFrom("parent").connectTo("children").maxDepth(5).restrict(Criteria.where("name").is("child1")).as("children");
		GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder().from("division").startWith("$parent").connectFrom("parent").connectTo("children").maxDepth(5).restrict(Criteria.where("_id").is(id)).as("children");
//		Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("id").is(id)), graphLookupOperation);

//		TypedAggregation<Division> aggregationPipeline = Aggregation.newAggregation(Division.class, graphLookupOperation);
//		List<Division> results = mongoTemplate.aggregate(aggregationPipeline, Division.class).getMappedResults();
		
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("parent22").exists(false)), graphLookupOperation);
	    List<Division> results = mongoTemplate.aggregate(aggregation, "division", Division.class).getMappedResults();

		return results;
	}

}
