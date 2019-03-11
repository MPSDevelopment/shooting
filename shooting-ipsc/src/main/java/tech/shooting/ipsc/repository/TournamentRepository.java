package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.ipsc.pojo.Tournament;

import java.util.List;

@Repository
public interface TournamentRepository extends MongoRepository<Tournament, Long> {

    public List<Tournament> findByName (String value);

}
