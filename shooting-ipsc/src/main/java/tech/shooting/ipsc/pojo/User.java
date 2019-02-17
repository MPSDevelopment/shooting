package tech.shooting.ipsc.pojo;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.enums.RoleName;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "user")
@TypeAlias("user")
@Data
@Accessors(chain = true)
public class User extends BaseDocument {

	public static final String EMAIL_FIELD = "email";

	@JsonProperty
	@ApiModelProperty(value = "User's login", required = true)
	@Indexed(unique = true)
	private String login;

	@JsonProperty
	@ApiModelProperty(value = "User's email", required = true)
	@Indexed(unique = true)
	private String email;

	@JsonProperty(access = Access.WRITE_ONLY)
	@ApiModelProperty(value = "User's password", required = true)
	private String password;

	@JsonProperty
	@ApiModelProperty(value = "User's birthday")
	private OffsetDateTime birthDate;

	@JsonProperty
	@ApiModelProperty(value = "User's surname", required = true)
	private String familyName;

	@JsonProperty
	@ApiModelProperty(value = "User's middle name")
	private String middleName;

	@JsonProperty
	@ApiModelProperty(value = "User's name", required = true)
	private String givenName;

	@JsonProperty
	@ApiModelProperty(value = "User's role name", required = true)
	private RoleName roleName;

	@JsonProperty(access = Access.WRITE_ONLY)
	@ApiModelProperty(value = "User's active ")
	private boolean active = true;
}
