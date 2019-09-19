package tech.shooting.ipsc.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;

import java.util.List;

@Slf4j
class CustomPersonRepositoryImpl implements CustomPersonRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Person> findByDivisionId(Long id) {
		// GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder().from("devision").startWith("parent").connectFrom("parent").connectTo("children").restrict(Criteria.where("id").is(id)).as // GraphLookupOperation
		// graphLookupOperation = GraphLookupOperation.builder().from("devision").startWith("parent").connectFrom("parent").connectTo("children").restrict(Criteria.where("id").is(id)).as
		// ("divisions"); // ("divisions");
		// Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("to.refId").is(id)), graphLookupOperation); // Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("to.refId").is(id)),
		// graphLookupOperation);
		// //
		// TypedAggregation<Division> aggregationPipeline = Aggregation.newAggregation(Division.class, graphLookupOperation); // TypedAggregation<Division> aggregationPipeline = Aggregation.newAggregation(Division.class,
		// graphLookupOperation);
		// List<Division> threadedPosts = mongoTemplate.aggregate(aggregationPipeline, Division.class).getMappedResults(); // List<Division> threadedPosts = mongoTemplate.aggregate(aggregationPipeline, Division.class).getMappedResults();

		LookupOperation lookupOperation = LookupOperation.newLookup().from("division").localField("divisionId").foreignField("id").as("divisions");

		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("divisionId").is(id)), lookupOperation);

		List<Person> results = mongoTemplate.aggregate(aggregation, "person", Person.class).getMappedResults();

		return results;
	}

	@Override
	public List<Division> findByDivisionIdRecursive(Long id) {

		GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder().from("division").startWith("_id").connectFrom("childrenid").connectTo("_id").restrict(Criteria.where("_id").is(id)).as("divisions");

		// Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("to.refId").is(id)), graphLookupOperation);
		//
		// TypedAggregation<Division> aggregationPipeline = Aggregation.newAggregation(Division.class, graphLookupOperation);
		// List<Division> threadedPosts = mongoTemplate.aggregate(aggregationPipeline, Division.class).getMappedResults();

//		UnwindOperation unwindOperation = Aggregation.unwind("divisions");

		MatchOperation match = Aggregation.match(Criteria.where("_id").is(id));
		
		Aggregation aggregation = Aggregation.newAggregation(graphLookupOperation);

		var results = mongoTemplate.aggregate(aggregation, "division", Division.class).getMappedResults();

		return results;
	}
}
