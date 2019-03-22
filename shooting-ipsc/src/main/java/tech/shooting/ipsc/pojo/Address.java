package tech.shooting.ipsc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import tech.shooting.commons.annotation.ValiationExportable;

@Data
@Accessors(chain = true)
public class Address implements ValiationExportable {
	public String street;

	public String city;

	public String region;

	public String index;

	public String phone;
}
