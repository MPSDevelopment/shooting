package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.ExerciseWeaponTypeEnum;

import java.time.OffsetDateTime;


@Document(collection = "person")
@TypeAlias("person")
@Data
@Accessors(chain = true)
public class Person extends BaseDocument {

    @JsonProperty
    @ApiModelProperty(value = "Person's name", required = true)
    private String name;

    @JsonProperty
    @ApiModelProperty(value = "Person's birthday")
    private OffsetDateTime birthDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "Person's active ")
    private boolean active = true;

    @JsonProperty
    @ApiModelProperty(value = "Person's address", required = true)
    private Address address;

    @JsonProperty
    @ApiModelProperty(value = "Person's team")
    private String team;

    @JsonProperty
    @ApiModelProperty(value = "Person's rank")
    private String rank;

    @JsonProperty
    @ApiModelProperty(value = "Person's IPSC code")
    private String codeIPSC;

    @JsonProperty
    @ApiModelProperty(value = "Person's type weapon")
    private ExerciseWeaponTypeEnum typeWeapon;

    @JsonProperty
    @ApiModelProperty(value = "Person's qualifier rank")
    private String qualifierRank;
}
