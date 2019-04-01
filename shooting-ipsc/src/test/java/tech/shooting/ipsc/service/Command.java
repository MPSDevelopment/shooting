package tech.shooting.ipsc.service;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Command {

	private String name;

}
