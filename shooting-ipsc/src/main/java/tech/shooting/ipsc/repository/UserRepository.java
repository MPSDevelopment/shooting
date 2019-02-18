package tech.shooting.ipsc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

	public User findByEmail(String value);
	
	public User findByLogin(String value);

	public User getById(Long id);

	public List<User> findByRoleName(RoleName roleName);

	public List<User> findByFirstName(String name) ;

	public List<User> findByLastName(String name);

	public User findByEmailAndActive(String email, boolean active);

}
