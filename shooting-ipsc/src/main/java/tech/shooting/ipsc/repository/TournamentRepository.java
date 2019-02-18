package tech.shooting.ipsc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.Tournament;
import tech.shooting.ipsc.pojo.User;

@Repository
public interface TournamentRepository extends MongoRepository<Tournament, Long> {

	public List<Tournament> findByName(String value);

}
