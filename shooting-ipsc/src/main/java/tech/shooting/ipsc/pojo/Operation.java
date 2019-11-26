package tech.shooting.ipsc.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.shooting.commons.mongo.BaseDocument;

@Document(collection = "operation")
@TypeAlias("operation")
@Data
@Accessors(chain = true)
public class Operation extends BaseDocument {

	public static final String WEATHER_FIELD = "weather";

	public static final String PARTICIPANTS_FIELD = "participants";

	public static final String MAIN_INDICATORS_FIELD = "mainIndicators";

	public static final String SYMBOLS_FIELD = "symbols";
	
	public static final String SIGNALS_FIELD = "signals";
	
	public static final String COMMANDANT_SERVICES_FIELD = "commandantServices";

	@JsonProperty
	@ApiModelProperty(value = "Operation info", required = true)
	private Info info;

	@JsonProperty
	@ApiModelProperty(value = "Operation weather", required = true)
	private Weather weather;

	@JsonProperty
	@ApiModelProperty(value = "Operation image path", required = true)
	private String imagePath;

	@JsonProperty
	@ApiModelProperty(value = "Operation participant list", required = true)
	private List<OperationParticipant> participants = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Operation symbols list", required = true)
	private List<OperationSymbol> symbols = new ArrayList<>();

	@JsonProperty
	@ApiModelProperty(value = "Operation main indicator list", required = true)
	private List<OperationMainIndicator> mainIndicators = new ArrayList<>();
	
	@JsonProperty
	@ApiModelProperty(value = "Operation combat signal list", required = true)
	private List<OperationSignal> signals = new ArrayList<>();
	
	@JsonProperty
	@ApiModelProperty(value = "Operation commandant service list", required = true)
	private List<OperationCommandantService> commandantServices = new ArrayList<>();

}
