package tech.shooting.tag.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.tag.enums.RoleName;

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "TokenUser", description = "Token simple user object")
@EqualsAndHashCode(callSuper = false)
@ToString
public class TokenUser {
	
	public static final String ID_FIELD = "id";
	public static final String LOGIN_FIELD = "login";
	public static final String ROLE_FIELD = "userrole";
	public static final String ORGANIZATION_FIELD = "userToOrganization";

	@JsonProperty
	@ApiModelProperty(value = "User's id")
	private Long id;
	
	@JsonProperty
	@ApiModelProperty(value = "User's login")
	private String login;

	@ApiModelProperty(value = "User's role")
	@JsonProperty
	private RoleName roleName;

}
