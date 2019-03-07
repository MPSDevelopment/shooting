package tech.shooting.commons.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserSignup {

	private String name;
	
	private String password;
}
