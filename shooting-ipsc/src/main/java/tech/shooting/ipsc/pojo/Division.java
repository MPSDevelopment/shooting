package tech.shooting.ipsc.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Division {
	private String root;

	private String name;

	List<Division> child;
}
