package tech.shooting.ipsc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.pojo.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
	User findByLogin (String value);
	List<User> findByRoleName (RoleName roleName);
	List<User> findByName (String name);
	User findByLoginAndActive (String login, boolean active);
	void deleteByRoleName (RoleName roleName);
	Page<User> findAllByRoleName(String role , PageRequest pageable);
}
