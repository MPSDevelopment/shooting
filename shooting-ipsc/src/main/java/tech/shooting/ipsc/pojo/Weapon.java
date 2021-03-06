package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.validator.ValidationConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Document(collection = "weapon")
@TypeAlias("weapon")
@Data
@Accessors(chain = true)
public class Weapon extends BaseDocument {
	
	public static final String OWNER_FIELD = "owner";

    @JsonProperty
    @Indexed(unique = true)
    @ApiModelProperty(value = "Serial number of weapon", required = true)
    @NotBlank(message = ValidationConstants.SERIAL_NUMBER_MESSAGE )
    @Min(value = 7,message = ValidationConstants.SERIAL_NUMBER_MESSAGE)
    //example AK-74 №4405222
    private String serialNumber;

    @JsonProperty("weaponName")
    @ApiModelProperty(value = "Weapon type", required = true)
    @DBRef
    //AK-47, AK-74,AKC-74,AKM,ПКМ, РПГ, АГС, ПМ,АПС,АПБ И ПРОЧЕЕ НАДО ВЗЯТЬ СПИСОК ИЛИ ДЕЛАТЬ Записью в базе
    private WeaponType weaponType;

    @JsonProperty
    @ApiModelProperty(value = "Fired count by weapon")
    @PositiveOrZero(message = ValidationConstants.VEHICLE_COUNT_MESSAGE)
    private Integer count;

    @DBRef
    @JsonProperty
    @ApiModelProperty(value = "Owner weapon")
    private Person owner;
}