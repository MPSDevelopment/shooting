package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;

@Document(collection = "user")
@TypeAlias("user")
@Data
@Accessors(chain = true)
public class User extends BaseDocument {
	public static final String LOGIN_FIELD = "login";

	public static final String PASSWORD_FIELD = "password";
	public static final String PASSWORD_ROLE_NAME = "roleName";

	@JsonProperty("userName")
	@ApiModelProperty(value = "User's name", required = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "User's login", required = true)
	@Indexed(unique = true)
	private String login;

	@JsonProperty(access = Access.WRITE_ONLY)
	@ApiModelProperty(value = "User's password", required = true)
	private String password;

	@JsonProperty
	@ApiModelProperty(value = "User's birthday")
	private OffsetDateTime birthDate;

	@JsonProperty
	@ApiModelProperty(value = "User's role name", required = true)
	private RoleName roleName;

	@JsonProperty
	@ApiModelProperty(value = "User's active ")
	private boolean active = true;

	@JsonProperty
	@ApiModelProperty(value = "User's address", required = true)
	private Address address;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Person id")
	private Person person;
}
