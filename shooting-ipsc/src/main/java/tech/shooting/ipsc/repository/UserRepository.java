package tech.shooting.ipsc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import tech.shooting.commons.enums.RoleName;
import tech.shooting.ipsc.pojo.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
	
	public User findByLogin(String value);

	public User getById(Long id);

	public List<User> findByRoleName(RoleName roleName);

	public List<User> findByName(String name);

	public User findByLoginAndActive(String login, boolean active);
	
	public void deleteByRoleName(RoleName roleName);

}
