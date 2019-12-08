package tech.shooting.tag.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.tag.enums.RoleName;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class TokenUser {
	
	public static final String ID_FIELD = "id";
	public static final String LOGIN_FIELD = "login";
	public static final String ROLE_FIELD = "userrole";
	public static final String ORGANIZATION_FIELD = "userToOrganization";

	@JsonProperty
	private Long id;
	
	@JsonProperty
	private String login;

	@JsonProperty
	private RoleName roleName;

}
