package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;
import tech.shooting.ipsc.enums.ExerciseWeaponTypeEnum;

/**
 * СТРЕЛКОВОЕ ЗАДАНИЕ. Отдельная, выполняемая спортсменом на время и с учетом
 * выбитых очков, мишенная обстановка, задуманная и построенная в соответствии с
 * заявленными ОЮЛ «Казахстанская Ассоциация Практической Стрельбы» принципами
 * построения упражнений, содержащая мишени и задания, которые каждый участник
 * должен безопасно преодолеть.
 * <p>
 * Часть соревнования ОЮЛ «Казахстанская Ассоциация Практической
 * Стрельбы», содержащая одно Стрелковое Задание и имеющее в непосредственной
 * близости сооружения, предназначенные для удобства стрелков, такие как навесы,
 * пирамиды и информационные щиты. Упражнение должно быть предназначено только для
 * отдельного вида оружия (например, упражнение для пистолета, или упражнение для
 * ружья, или упражнение для карабина).
 *
 * @author Viking
 */

@Document(collection = "exercise")
@TypeAlias("exercise")
@Data
@Accessors(chain = true)
public class Exercise extends BaseDocument {

	@JsonProperty
	@ApiModelProperty(value = "Exercise's name", required = true)
	@Indexed(unique = true)
	private String name;

	@JsonProperty
	@ApiModelProperty(value = "Exercise's weapon type", required = true)
	private ExerciseWeaponTypeEnum weaponType;

}
