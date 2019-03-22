package tech.shooting.commons.mongo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import tech.shooting.commons.annotation.ValiationExportable;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode()
public class BaseDocument implements ShootingDocument, ValiationExportable {

	public static final String ID_FIELD = "id";

	public static final String ID_FIELD_GS = "id";

	public static final String CREATED_DATE_FIELD = "createdDate";

	public static final String UPDATED_DATE_FIELD = "updatedDate";

	@Id
	@JsonProperty(ID_FIELD_GS)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long id;

	@CreatedDate
	protected OffsetDateTime createdDate;

	@LastModifiedDate
	protected OffsetDateTime updatedDate;

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof BaseDocument && id != null) {
			return id.equals(((BaseDocument) object).getId());
		}
		return false;
	}
}
