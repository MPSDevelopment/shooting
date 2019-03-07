package tech.shooting.commons.utils;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

	@JsonProperty("n")
	@JsonView(TestViews.Public.class)
	private String name;

	@JsonProperty("s")
	@JsonView(TestViews.Public.class)
	private String surname;

	@JsonProperty("c")
	@JsonView(TestViews.Private.class)
	private Integer count;
	
	@JsonProperty("sel")
	private Long salary;
	
	@JsonIgnore
	private String password;
	
	@JsonProperty("date")
	private Date date;
}
