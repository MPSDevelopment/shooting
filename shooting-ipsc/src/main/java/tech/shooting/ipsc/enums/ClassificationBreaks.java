package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.pojo.LevelBean;

import java.util.ArrayList;
import java.util.List;

public enum ClassificationBreaks {
	D("Breaks in range 0% to 39.99999%", 0.00F, 39.99999F),
	C("Breaks in range 40% to 59.9999%", 40.00F, 59.9999F),
	B("Breaks in range 60% to 74.9999%", 60.00F, 74.99999F),
	A("Breaks in range 75% to 84.9999%", 75.00F, 84.99999F),
	Master("Breaks in range 85% to 94.9999%", 85.00F, 94.99999F),
	GrandMaster("Breaks in range 95% to " + "100%", 95.00F, 100.00F);

	private String value;

	private Float min;

	private Float max;

	ClassificationBreaks (String value, Float min, Float max) {
		this.value = value;
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString () {
		return String.valueOf(value);
	}

	public String getValue () {
		return value;
	}

	public static List<LevelBean> getList () {
		List<LevelBean> result = new ArrayList<>();
		for(ClassificationBreaks value : ClassificationBreaks.values()) {
			result.add(new LevelBean().setClassificationBreaks(value).setDescription(value.value).setMin(value.min).setMax(value.max));
		}
		return result;
	}

	public static Integer getCount () {
		return ClassificationBreaks.values().length;
	}
}
