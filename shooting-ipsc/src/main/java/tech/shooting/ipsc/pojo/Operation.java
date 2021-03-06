package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "operation")
@TypeAlias("operation")
@Getter
@Setter
@Accessors(chain = true)
public class Operation extends BaseDocument {

	public static final String WEATHER_FIELD = "weather";

	public static final String PARTICIPANTS_FIELD = "participants";

	public static final String MAIN_INDICATORS_FIELD = "mainIndicators";

	public static final String SYMBOLS_FIELD = "symbols";
	
	public static final String SIGNALS_FIELD = "signals";
	
	public static final String COMMANDANT_SERVICES_FIELD = "commandantServices";
	
	public static final String COMBAT_ELEMENTS_FIELD = "combatElements";
	
	public static final String ROUTES_FIELD = "routes";

	@JsonProperty
	@ApiModelProperty(value = "Operation info", required = true)
	private Info info;

	@JsonProperty
	@ApiModelProperty(value = "Operation weather")
	private Weather weather;

	@JsonProperty
	@ApiModelProperty(value = "Operation image path")
	private String imagePath;

	@DBRef
	@JsonProperty
	@ApiModelProperty(value = "Operation participant list")
	private List<Person> participants = new ArrayList<>();
	
	@JsonProperty
	@ApiModelProperty(value = "Operation combat elements")
	private List<OperationCombatElement> combatElements = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Operation symbols list")
	private List<OperationSymbol> symbols = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Operation main indicator list")
	private List<OperationMainIndicator> mainIndicators = new ArrayList<>();
	
	@JsonProperty
	@ApiModelProperty(value = "Operation combat signal list")
	private List<OperationSignal> signals = new ArrayList<>();
	
	@JsonProperty
	@ApiModelProperty(value = "Operation commandant service list")
	private List<OperationCommandantService> commandantServices = new ArrayList<>();
	
	@JsonProperty
	@ApiModelProperty(value = "Operation routes")
	private List<OperationRoute> routes = new ArrayList<>();

}
