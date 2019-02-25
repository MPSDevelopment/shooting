package tech.shooting.ipsc.db;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mpsdevelopment.plasticine.commons.IdGenerator;

import tech.shooting.ipsc.pojo.User;

@Component
public class UserDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void upsert(User user) {
		// encode password
		user.setPassword(user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : null);

		var query = Query.query(Criteria.where(User.LOGIN_FIELD).is(user.getLogin()));

		var update = new Update().set(User.UPDATED_DATE_FIELD, OffsetDateTime.now(ZoneOffset.UTC)).setOnInsert(User.ID_FIELD, IdGenerator.nextId()).setOnInsert(User.CREATED_DATE_FIELD, OffsetDateTime.now(ZoneOffset.UTC));
		mongoTemplate.upsert(query, update, User.class);
	}
}
