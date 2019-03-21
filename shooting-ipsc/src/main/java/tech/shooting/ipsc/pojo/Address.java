package tech.shooting.ipsc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Address {
	public String street;

	public String city;

	public String region;

	public String index;

	public String phone;
}
