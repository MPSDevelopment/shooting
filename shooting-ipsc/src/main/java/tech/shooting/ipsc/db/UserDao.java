package tech.shooting.ipsc.db;

import com.mpsdevelopment.plasticine.commons.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.shooting.ipsc.pojo.User;
import tech.shooting.ipsc.repository.UserRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class UserDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public User createIfNotExists(User user) {
		User dbUser = userRepository.findByLogin(user.getLogin());
		if (dbUser == null) {
			// encode password
			user.setPassword(user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : null);
			dbUser = userRepository.save(user);
		}
		return dbUser;
	}

	public void upsert(User user) {
		// encode password
		user.setPassword(user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : null);
		var query = Query.query(Criteria.where(User.LOGIN_FIELD).is(user.getLogin()));
		var update = new Update().set(User.UPDATED_DATE_FIELD, OffsetDateTime.now(ZoneOffset.UTC)).setOnInsert(User.ID_FIELD, IdGenerator.nextId()).setOnInsert(User.CREATED_DATE_FIELD, OffsetDateTime.now(ZoneOffset.UTC))
				.setOnInsert(User.PASSWORD_FIELD, user.getPassword());
		mongoTemplate.upsert(query, update, User.class);
	}
	// public void createIfNotExists(User user) {
	// var query = Query.query(Criteria.where(User.LOGIN_FIELD).is(user.getLogin()));
	// var update = new Update().set(User.UPDATED_DATE_FIELD, OffsetDateTime.now(ZoneOffset.UTC));
	// User p = mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true).upsert(true), User.class);
	// }
}
