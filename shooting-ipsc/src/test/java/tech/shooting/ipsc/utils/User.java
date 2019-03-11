package tech.shooting.ipsc.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

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
