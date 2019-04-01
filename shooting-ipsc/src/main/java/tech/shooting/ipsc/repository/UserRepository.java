package tech.shooting.ipsc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
	User findByLogin (String value);

	List<User> findByRoleName (RoleName roleName);

	List<User> findByName (String name);

	User findByLoginAndActive (String login, boolean active);

	void deleteByRoleName (RoleName roleName);

	User findByPerson (Person person);
}
